package com.mysiteforme.admin.service.impl;

import com.alibaba.fastjson2.JSON;
import com.mysiteforme.admin.config.ChatRedisMemory;
import com.mysiteforme.admin.entity.DTO.ChatDTO;
import com.mysiteforme.admin.entity.DTO.ChatHistoryDTO;
import com.mysiteforme.admin.entity.aiEntity.BusinessRequirement;
import com.mysiteforme.admin.entity.request.ChatRequest;
import com.mysiteforme.admin.redis.RedisConstants;
import com.mysiteforme.admin.redis.RedisUtils;
import com.mysiteforme.admin.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Slf4j
@Service
public class ChatServiceImpl implements ChatService {

    private final RedisUtils redisUtils;

    private final ChatClient client;

    private final Integer CHAT_HISTORY_SIZE = 100;

    private final String systemAnalysisPromptText = """
        你是一位经验丰富的软件架构师和数据库设计专家。
        用户的请求是关于开发一个新的软件模块。你的任务是：
        1. 仔细理解用户的初步需求。
        2. 基于该需求，全面地扩充它。具体来说，你需要思考并详细描述：
           - 该模块通常需要哪些核心的数据库表？
           - 每个表应该包含哪些字段？请列出字段名，并为每个字段建议一个合适的数据类型（例如 VARCHAR(255), TEXT, INT, BIGINT, BOOLEAN, TIMESTAMP, DATE）。
           - 指出每个表的主键是什么。
           - 哪些字段可以为空，哪些字段不能为空。
           - 提供每个字段的简短描述，说明其用途。
           - 表与表之间可能存在哪些关联关系（例如：一对一, 一对多, 多对多）？如果是一对多或多对多，请指明外键字段。
        3. 你的输出应该是一段清晰、详细的文本描述，涵盖以上所有要点。不要输出JSON，只需输出文本描述。
        你的输出将直接展示给用户，所以请确保内容友好且易于理解。
        """;

    private final String systemToolCallPromptText = """
        你是一个AI助手，擅长从文本中精确提取结构化数据并调用外部工具。
        当前的会话ChatId的值为: %s
        你将收到一段详细描述软件模块（包含表、字段、关系等信息）的文本。
        你的任务是：
        1. 仔细阅读提供的文本描述。
        2. 从文本中提取所有相关的结构化信息，并将其填充到 'BlogModuleSchema' 结构中。
           你需要提取模块的名称 (可以根据描述给一个合适的名字，例如 '博客系统数据库设计')，
           所有表的定义 (TableDefinition)，每个表的字段定义 (FieldDefinition)，以及表之间的关系 (RelationDefinition)。
        3. 确保准确提取每个字段的名称 (fieldName)、数据类型 (fieldType)、是否主键 (primaryKey)、是否可空 (nullable) 和描述 (description)。
        4. 准确提取表之间的关系，包括关系类型 (type)、源字段 (fromField)、目标表 (toTable) 和目标字段 (toField)。
        5. 准备好这些数据后，调用名为 'blogModuleGeneratorTool' 的工具，并将填充好的 'BlogModuleSchema' 对象作为参数传递给它。
        在调用工具后，根据工具的返回信息，给出一个简洁的总结。
        """;

    public ChatServiceImpl(RedisUtils redisUtils, ChatClient client) {
        this.redisUtils = redisUtils;
        this.client = client;
    }

    @Override
    public List<ChatHistoryDTO> getHistoryList(String deviceId, int limit) {
        String deviceKey = RedisConstants.AI_DEVICE_HISTORY_KEY_PREFIX + deviceId;
        if(!redisUtils.hasKey(deviceKey)){
            return Collections.emptyList();
        }
        Set<Object> deviceChatIds = redisUtils.zRange(deviceKey,0,limit-1);
        return deviceChatIds.stream()
                .map(o -> {
                    if(o instanceof String chatId) {
                        ChatHistoryDTO chatHistoryDTO = new ChatHistoryDTO();
                        chatHistoryDTO.setChatId(chatId);
                        String historyKey = RedisConstants.AI_HISTORY_KEY_PREFIX + chatId;
                        List<Object> historyList = redisUtils.lRange(historyKey, 0, -1);
                        List<ChatDTO> chatList =  historyList.stream().map(h -> {
                            if(h instanceof ChatDTO chat){
                                return chat;
                            }
                            return null;
                        })
                        .filter(Objects::nonNull)
                        .toList();
                        chatHistoryDTO.setChatList(chatList);
                        return chatHistoryDTO;
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public Flux<String> getMessage(ChatRequest request) {
        ChatRedisMemory chatRedisMemory = new ChatRedisMemory(redisUtils,request.getDeviceId());
        return client.prompt(request.getContent())
                .advisors(new MessageChatMemoryAdvisor(chatRedisMemory))
                .advisors(a -> a
                        .param(CHAT_MEMORY_CONVERSATION_ID_KEY, request.getChatId())
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, CHAT_HISTORY_SIZE)
                )
                .stream()
                .content()
                .onErrorResume(e -> {
                    log.error("流式响应异常 (after retries or non-retryable) for chatId {}: {}", request.getChatId(), e.getMessage(), e);
                    return Flux.just("[系统提示] AI 服务通讯异常，请稍后重试或联系管理员。");
                });
    }

    /**
     * 从用户对话中提取业务需求信息
     */
    @Override
    public Flux<String> analyzeUserRequirements(ChatRequest request) {
        ChatRedisMemory chatRedisMemory = new ChatRedisMemory(redisUtils, request.getDeviceId());
        // 创建分析需求的系统提示
        String systemPrompt = """
            你是一个资深的业务需求分析师，专门负责从用户的自然语言描述中提取准确的业务需求。
            当前的会话ChatId的值为: %s
            你的任务是：
            1. 理解用户想要开发的模块功能
            1.1 从你的理解中生成模块名称,模块描述
            1.2 **必须调用**[保存业务需求信息]函数保存业务信息。如果无法提取模块名称或描述，请说明。
            1.3 业务需求信息数据结构如下:
            {
              "chatId": "当前聊天会话ID",
              "moduleName": "模块名称",
              "moduleDescription": "模块描述",
            }
            2. 识别核心业务实体（如用户、订单、产品等）
            2.1 如果从用户输入中识别出任何核心业务实体，**必须调用**[保存新增实体信息]函数为每一个识别出的实体保存信息。
            2.2 实体信息的数据结构如下:
            {
              "chatId": "当前聊天会话ID",
              "entityName": "实体名称",
              "entityDescription": "实体描述",
              "fields": [
                {
                  "fieldName": "字段名",
                  "fieldDescription": "字段描述",
                  "dataType": "数据类型建议",
                  "required": true/false,
                  "defaultValue": "默认值（如有）",
                  "businessRules": ["相关业务规则"]
                }
              ]
            }
            3. 推断实体之间的关联关系
            3.1 如果识别出实体间的关联关系，**必须调用**[保存实体之间的关系信息]函数保存这些关系信息。
            3.2 实体之间的关联关系数据结构如下:
            {
              "chatId": "当前聊天会话ID",
              "fromEntity": "源实体",
              "toEntity": "目标实体",
              "relationshipType": "一对一/一对多/多对多",
              "description": "关系描述"
            }
            4. 补充用户未明确提及但业务必需的字段
            4.1 如果基于你的业务经验推断出任何额外的需求或必要字段，**必须调用**[保存额外需求信息]函数来保存这些额外需求信息。
            4.2 额外需求的数据结构如下:
            {
              "chatId": "当前聊天会话ID",
              "additionalRequirement": "基于经验推断的额外需求"
            }
            用自然语言向用户解释你对需求的理解,并根据任务进度分别调用相应函数辅助完成数据收集。
            **重要指令**：
            - 你必须按顺序处理任务1到4。
            - 对于每个任务中指定的函数调用点（1.2, 2.1, 3.1, 4.1），如果从用户输入中分析得到相应内容，则**必须执行函数调用**。
            - **在完成所有必要的函数调用之后**，才用自然语言向用户解释你对需求的整体理解以及你进行了哪些数据保存操作。
            - 最后，根据已分析的业务需求，生成需要向用户确认的问题。
            
            生成问题的原则：
            1. 确认关键业务逻辑是否正确
            2. 询问可能存在歧义的字段含义
            3. 确认数据类型是否合适
            4. 询问是否需要额外的业务字段
            5. 确认表之间的关联关系
            
            每个元素是一个确认问题。
            
            基于刚刚的业务需求分析结果，生成5-8个关键的确认问题：
            请生成具体、清晰的确认问题，帮助完善需求分析。
            
            """.formatted(request.getChatId());

        String userPrompt = """
            请分析以下用户需求描述，提取业务需求和数据结构信息：
            用户需求：%s
            请根据上述需求，分析出需要的数据表结构，包括表名、字段、数据类型等信息。
            如果用户没有明确说明某些必要的字段，请根据业务常识进行补充。
        """.formatted(request.getContent());

        return client.prompt()
                .system(systemPrompt)
                .user(userPrompt)
                .advisors(new MessageChatMemoryAdvisor(chatRedisMemory))
                .advisors(a -> a
                        .param(CHAT_MEMORY_CONVERSATION_ID_KEY, request.getChatId())
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, CHAT_HISTORY_SIZE)
                )
                .tools("saveRequirement",
                        "saveEntity",
                        "saveRelationship",
                        "saveAdditional")
                .stream()
                .content()
                .doOnError(e -> log.error("流处理错误 (chatId: {}): {}", request.getChatId(), e.getMessage(), e))
                .doFinally( signalType -> {

                })
                .onErrorResume(e -> {
                    log.error("流式响应异常 for chatId {}: {}", request.getChatId(), e.getMessage(), e);
                    return Flux.just("[系统提示] AI 服务通讯异常，请稍后重试或联系管理员。");
                });

    }

    /**
     * 收集用户确认和补充信息
     */
    @Override
    public Flux<String> generateConfirmationQuestions(ChatRequest request) {
        log.debug("开始进行用户确认和补充信息");
        ChatRedisMemory chatRedisMemory = new ChatRedisMemory(redisUtils, request.getDeviceId());
        String systemPrompt = """
            你是一个业务需求确认专家。根据已分析的业务需求，生成需要向用户确认的问题。
            
            生成问题的原则：
            1. 确认关键业务逻辑是否正确
            2. 询问可能存在歧义的字段含义
            3. 确认数据类型是否合适
            4. 询问是否需要额外的业务字段
            5. 确认表之间的关联关系
            
            每个元素是一个确认问题。
            """;

        String userPrompt = """
            基于刚刚的业务需求分析结果，生成5-8个关键的确认问题：
            请生成具体、清晰的确认问题，帮助完善需求分析。
            """;

        Flux<String> response = client.prompt()
                .system(systemPrompt)
                .user(userPrompt)
                .advisors(new MessageChatMemoryAdvisor(chatRedisMemory))
                .advisors(a -> a
                        .param(CHAT_MEMORY_CONVERSATION_ID_KEY, request.getChatId())
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, CHAT_HISTORY_SIZE)
                )
                .stream()
                .content()
                .onErrorResume(e -> {
                    log.error("流式响应异常 (after retries or non-retryable) for chatId {}: {}", request.getChatId(), e.getMessage(), e);
                    return Flux.just("[系统提示] AI 服务通讯异常，请稍后重试或联系管理员。");
                });
        // 创建包含前缀消息的Flux
        Flux<String> prefixFlux = Flux.just("我已经分析了您的需求，下面是我的理解，请确认是否正确：\n");
        return Flux.concat(prefixFlux,response);
    }

    /**
     * 根据用户反馈优化需求
     */
    @Override
    @Tool(description = "根据用户反馈调整和优化业务需求")
    public BusinessRequirement refineRequirements(BusinessRequirement originalRequirement, String userFeedback) {
        String systemPrompt = """
            你是一个需求优化专家。根据用户的反馈意见，调整和优化原有的业务需求分析结果。
            
            优化原则：
            1. 根据用户反馈修正错误的理解
            2. 补充用户提到的遗漏信息
            3. 调整不合适的数据类型
            4. 修正错误的关联关系
            5. 保持JSON格式的完整性
            
            返回优化后的完整业务需求JSON。
            """;

        String userPrompt = """
            原始需求分析：
            %s
            
            用户反馈：
            %s
            
            请根据用户反馈优化需求分析结果。
            """.formatted(JSON.toJSONString(originalRequirement), userFeedback);

        try {
            String response = client.prompt()
                    .system(systemPrompt)
                    .user(userPrompt)
                    .call()
                    .content();

            return JSON.parseObject(response, BusinessRequirement.class);

        } catch (Exception e) {
            throw new RuntimeException("优化需求时发生错误: " + e.getMessage(), e);
        }
    }
}

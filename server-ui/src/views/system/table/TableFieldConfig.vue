<template>
  <div class="field-config-container">
    <el-dialog
      :model-value="visible"
      :title="$t('tableFieldConfig.dialog.title', { tableName: tableConfig?.tableName || '' })"
      width="1500"
      :append-to-body="true"
      :destroy-on-close="false"
      align-center
      @closed="handleClose"
      @update:model-value="(val) => emit('update:visible', val)"
    >
      <ArtTableFullScreen>
        <div id="table-full-screen">
          <!-- 搜索栏 -->
          <ArtSearchBar
            v-model:filter="formFilters as any"
            :items="formItems"
            @reset="handleReset"
            @search="handleSearch"
            :isExpand="false"
          ></ArtSearchBar>

          <ElCard shadow="never" class="art-table-card">
            <!-- 表格头部 -->
            <ArtTableHeader
              :columnList="columnOptions"
              v-model:columns="columnChecks"
              @refresh="loadFieldData"
            >
              <template #left>
                <ElButton
                  type="primary"
                  v-if="!sortMode"
                  @click="handleSyncFields"
                  v-auth="'tableconfig_sync_field'"
                  v-ripple
                  >{{ $t('tableFieldConfig.button.syncFields') }}</ElButton
                >
                <ElButton
                  :type="sortMode ? 'danger' : 'info'"
                  @click="handleFieldSort"
                  v-auth="'tableconfig_field_sort'"
                  v-ripple
                  >{{
                    sortMode
                      ? $t('tableFieldConfig.button.exitSort')
                      : $t('tableFieldConfig.button.fieldSort')
                  }}</ElButton
                >
                <template v-if="sortMode">
                  <ElButton
                    type="success"
                    @click="handleSaveSortOrder"
                    v-auth="'tableconfig_field_sort'"
                    v-ripple
                    >{{ $t('tableFieldConfig.button.saveSort') }}</ElButton
                  >
                  <ElButton @click="handleCancelSort" v-auth="'tableconfig_field_sort'" v-ripple>{{
                    $t('tableFieldConfig.button.cancelSort')
                  }}</ElButton>
                </template>
                <ElButton
                  type="success"
                  v-if="!sortMode"
                  @click="handlePreviewTable"
                  v-auth="'tableconfig_perview_form'"
                  v-ripple
                  >{{ $t('tableFieldConfig.button.previewForm') }}</ElButton
                >
              </template>
            </ArtTableHeader>
            <!-- 表格 -->
            <VueDraggable
              v-model="internalFieldList"
              target="tbody"
              handle=".handle"
              :animation="150"
              :disabled="!sortMode"
              item-key="id"
              class="draggable-container"
            >
              <ArtTable
                :data="internalFieldList"
                v-loading="fieldListLoading"
                :currentPage="formFilters.page"
                :pageSize="formFilters.limit"
                :total="pagination.total"
                @current-change="handleCurrentChange"
                @size-change="handleSizeChange"
                height="100%"
                :marginTop="10"
              >
                <template #default>
                  <ElTableColumn v-for="col in columns" :key="col.prop || col.type" v-bind="col" />
                </template>
              </ArtTable>
            </VueDraggable>
          </ElCard>
        </div>
      </ArtTableFullScreen>

      <!-- 添加编辑字段对话框 -->
      <el-dialog
        v-model="fieldDialogVisible"
        :title="fieldDialogTitle"
        width="850px"
        :append-to-body="true"
        :destroy-on-close="true"
        @closed="resetFieldForm"
      >
        <el-form ref="fieldFormRef" :model="fieldForm" label-width="100px" :rules="fieldFormRules">
          <el-row :gutter="12">
            <el-col :span="12">
              <el-form-item :label="$t('tableFieldConfig.form.fieldName')" prop="columnName">
                <el-input v-model="fieldInfo.columnName" disabled />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item :label="$t('tableFieldConfig.form.fieldType')" prop="columnType">
                <el-input v-model="fieldInfo.columnType" disabled />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="12">
            <el-col :span="12">
              <el-form-item :label="$t('tableFieldConfig.form.fieldComment')" prop="columnComment">
                <el-input v-model="fieldInfo.columnComment" disabled />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item :label="$t('tableFieldConfig.form.businessName')" prop="businessName">
                <el-input v-model="fieldForm.businessName" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="12">
            <el-col :span="12">
              <el-form-item
                :label="$t('tableFieldConfig.form.componentType')"
                prop="formComponentType"
              >
                <el-select
                  v-model="fieldForm.formComponentType"
                  :placeholder="$t('tableFieldConfig.form.inputComponentType')"
                  style="width: 100%"
                  clearable
                >
                  <el-option :label="$t('tableFieldConfig.componentType.input')" value="INPUT" />
                  <el-option
                    :label="$t('tableFieldConfig.componentType.textarea')"
                    value="TEXTAREA"
                  />
                  <el-option :label="$t('tableFieldConfig.componentType.select')" value="SELECT" />
                  <el-option
                    :label="$t('tableFieldConfig.componentType.inputNumber')"
                    value="INPUT_NUMBER"
                  />
                  <el-option :label="$t('tableFieldConfig.componentType.radio')" value="RADIO" />
                  <el-option
                    :label="$t('tableFieldConfig.componentType.checkbox')"
                    value="CHECKBOX"
                  />
                  <el-option
                    :label="$t('tableFieldConfig.componentType.colorPicker')"
                    value="COLOR_PICKER"
                  />
                  <el-option
                    :label="$t('tableFieldConfig.componentType.iconPicker')"
                    value="ICON_PICKER"
                  />
                  <el-option
                    :label="$t('tableFieldConfig.componentType.datePicker')"
                    value="DATE_PICKER"
                  />
                  <el-option
                    :label="$t('tableFieldConfig.componentType.timePicker')"
                    value="TIME_PICKER"
                  />
                  <el-option
                    :label="$t('tableFieldConfig.componentType.datetimePicker')"
                    value="DATETIME_PICKER"
                  />
                  <el-option :label="$t('tableFieldConfig.componentType.switch')" value="SWITCH" />
                  <el-option
                    :label="$t('tableFieldConfig.componentType.imageUpload')"
                    value="IMAGE_UPLOAD"
                  />
                  <el-option
                    :label="$t('tableFieldConfig.componentType.fileUpload')"
                    value="FILE_UPLOAD"
                  />
                  <el-option
                    :label="$t('tableFieldConfig.componentType.richText')"
                    value="RICH_TEXT"
                  />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item
                :label="$t('tableFieldConfig.form.validationRules')"
                prop="validationRules"
              >
                <el-input
                  v-model="fieldForm.validationRules"
                  :placeholder="$t('tableFieldConfig.form.validationRulesPlaceholder')"
                />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="12">
            <el-col :span="12">
              <el-form-item
                :label="$t('tableFieldConfig.form.associatedType')"
                prop="associatedType"
                :rules="[
                  {
                    required: isAssociatedTypeRequired,
                    message: $t('tableFieldConfig.validation.associatedTypeRequired'),
                    trigger: 'change'
                  }
                ]"
              >
                <el-select
                  v-model="fieldForm.associatedType"
                  :placeholder="$t('tableFieldConfig.form.selectAssociatedType')"
                  style="width: 100%"
                  @change="handleAssociatedTypeChange"
                  :required="isAssociatedTypeRequired"
                  clearable
                >
                  <el-option :label="$t('tableFieldConfig.associatedType.dictType')" :value="1" />
                  <el-option
                    :label="$t('tableFieldConfig.associatedType.associatedTable')"
                    :value="2"
                  />
                </el-select>
                <div
                  class="el-form-item-msg"
                  v-if="isAssociatedTypeRequired && !fieldForm.associatedType"
                >
                  <span class="warning-text">{{
                    $t('tableFieldConfig.warning.associatedTypeRequired')
                  }}</span>
                </div>
              </el-form-item>
            </el-col>
            <el-col :span="12" v-if="fieldForm.associatedType === 1">
              <el-form-item :label="$t('tableFieldConfig.form.dictType')" prop="associatedDictType">
                <el-select
                  v-model="fieldForm.associatedDictType"
                  :placeholder="$t('tableFieldConfig.form.selectDictType')"
                  style="width: 100%"
                  filterable
                  clearable
                  @change="handleDictTypeChange"
                >
                  <el-option
                    v-for="item in dictTypeList"
                    :key="item.type"
                    :label="item.type"
                    :value="item.type"
                  />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12" v-if="fieldForm.associatedType === 2">
              <el-form-item
                :label="$t('tableFieldConfig.form.associatedTable')"
                prop="associatedTable"
              >
                <el-select
                  v-model="fieldForm.associatedTable"
                  :placeholder="$t('tableFieldConfig.form.selectAssociatedTable')"
                  style="width: 100%"
                  filterable
                  clearable
                  @change="handleTableNameChange"
                >
                  <el-option
                    v-for="item in associatedTableList"
                    :key="item"
                    :label="item"
                    :value="item"
                  />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="12" v-if="fieldForm.associatedType === 2 && fieldForm.associatedTable">
            <el-col :span="12">
              <el-form-item
                :label="$t('tableFieldConfig.form.displayField')"
                prop="associatedTableField"
              >
                <el-select
                  v-model="fieldForm.associatedTableField"
                  :placeholder="$t('tableFieldConfig.form.selectDisplayField')"
                  style="width: 100%"
                  filterable
                  clearable
                >
                  <el-option
                    v-for="item in associatedTableFields"
                    :key="item.columnName || ''"
                    :label="`${item.columnName || ''}(${item.columnComment || '无注释'})`"
                    :value="item.columnName || ''"
                  />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="12">
            <el-col :span="6">
              <el-tooltip :content="$t('tableFieldConfig.tooltip.isUnique')" placement="top">
                <el-form-item :label="$t('tableFieldConfig.form.isUnique')" label-width="80px">
                  <el-switch v-model="fieldForm.isUnique"></el-switch>
                </el-form-item>
              </el-tooltip>
            </el-col>
            <el-col :span="6">
              <el-tooltip
                :content="
                  originalFieldData && originalFieldData.isNullable === false
                    ? $t('tableFieldConfig.tooltip.dbRequiredField')
                    : $t('tableFieldConfig.tooltip.isRequired')
                "
                placement="top"
              >
                <el-form-item :label="$t('tableFieldConfig.form.isRequired')" label-width="80px">
                  <el-switch
                    v-model="fieldForm.isNullable"
                    :active-value="false"
                    :inactive-value="true"
                    :disabled="
                      originalFieldData && originalFieldData.isNullable === false ? true : false
                    "
                  ></el-switch>
                </el-form-item>
              </el-tooltip>
            </el-col>
            <el-col :span="6">
              <el-tooltip :content="$t('tableFieldConfig.tooltip.isListVisible')" placement="top">
                <el-form-item :label="$t('tableFieldConfig.form.isListVisible')" label-width="80px">
                  <el-switch v-model="fieldForm.isListVisible"></el-switch>
                </el-form-item>
              </el-tooltip>
            </el-col>
            <el-col :span="6">
              <el-tooltip :content="$t('tableFieldConfig.tooltip.isAddVisible')" placement="top">
                <el-form-item :label="$t('tableFieldConfig.form.isAddVisible')" label-width="80px">
                  <el-switch v-model="fieldForm.isAddVisible"></el-switch>
                </el-form-item>
              </el-tooltip>
            </el-col>
          </el-row>

          <el-row :gutter="12">
            <el-col :span="6">
              <el-tooltip :content="$t('tableFieldConfig.tooltip.isEditVisible')" placement="top">
                <el-form-item :label="$t('tableFieldConfig.form.isEditVisible')" label-width="80px">
                  <el-switch v-model="fieldForm.isEditVisible"></el-switch>
                </el-form-item>
              </el-tooltip>
            </el-col>
            <el-col :span="6">
              <el-tooltip :content="$t('tableFieldConfig.tooltip.isDetailVisible')" placement="top">
                <el-form-item
                  :label="$t('tableFieldConfig.form.isDetailVisible')"
                  label-width="80px"
                >
                  <el-switch v-model="fieldForm.isDetailVisible"></el-switch>
                </el-form-item>
              </el-tooltip>
            </el-col>
            <el-col :span="6">
              <el-tooltip :content="$t('tableFieldConfig.tooltip.isQueryField')" placement="top">
                <el-form-item :label="$t('tableFieldConfig.form.isQueryField')" label-width="80px">
                  <el-switch v-model="fieldForm.isQueryField"></el-switch>
                </el-form-item>
              </el-tooltip>
            </el-col>
          </el-row>
          <el-row :gutter="12">
            <el-col :span="12">
              <el-form-item :label="$t('tableFieldConfig.form.sortValue')" prop="sort">
                <el-input-number
                  v-model="fieldForm.sort"
                  :min="0"
                  :max="9999"
                  controls-position="right"
                  :precision="0"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item
            :label="$t('tableFieldConfig.form.queryType')"
            prop="queryType"
            v-if="fieldForm.isQueryField"
          >
            <el-select
              v-model="fieldForm.queryType"
              :placeholder="$t('tableFieldConfig.form.inputComponentType')"
              style="width: 100%"
            >
              <el-option :label="$t('tableFieldConfig.queryType.eq')" value="eq" />
              <el-option :label="$t('tableFieldConfig.queryType.ne')" value="ne" />
              <el-option :label="$t('tableFieldConfig.queryType.gt')" value="gt" />
              <el-option :label="$t('tableFieldConfig.queryType.ge')" value="ge" />
              <el-option :label="$t('tableFieldConfig.queryType.lt')" value="lt" />
              <el-option :label="$t('tableFieldConfig.queryType.le')" value="le" />
              <el-option :label="$t('tableFieldConfig.queryType.like')" value="like" />
              <el-option :label="$t('tableFieldConfig.queryType.leftLike')" value="left_like" />
              <el-option :label="$t('tableFieldConfig.queryType.rightLike')" value="right_like" />
              <el-option :label="$t('tableFieldConfig.queryType.between')" value="between" />
              <el-option :label="$t('tableFieldConfig.queryType.in')" value="in" />
              <el-option :label="$t('tableFieldConfig.queryType.notIn')" value="not_in" />
              <el-option :label="$t('tableFieldConfig.queryType.isNotNull')" value="is_not_null" />
              <el-option :label="$t('tableFieldConfig.queryType.isNull')" value="is_null" />
            </el-select>
          </el-form-item>

          <el-form-item :label="$t('tableFieldConfig.form.remarks')" prop="remarks">
            <el-input
              v-model="fieldForm.remarks"
              type="textarea"
              :rows="3"
              :placeholder="$t('tableFieldConfig.form.inputRemarks')"
            />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="fieldDialogVisible = false">{{
            $t('tableFieldConfig.button.cancel')
          }}</el-button>
          <el-button type="primary" @click="submitFieldForm" :loading="submitLoading">{{
            $t('tableFieldConfig.button.confirm')
          }}</el-button>
        </template>
      </el-dialog>

      <!-- 添加预览对话框 -->
      <el-dialog
        v-model="previewVisible"
        :title="$t('tableFieldConfig.preview.title', { tableName: tableConfig?.tableName || '' })"
        width="90%"
        :append-to-body="true"
        :destroy-on-close="true"
        fullscreen
      >
        <!-- 使用预览组件 -->
        <TablePreview :tableConfig="tableConfig" :fieldList="internalFieldList" />
      </el-dialog>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
  import { computed, ref, reactive, watch, onMounted, h } from 'vue'
  import { ElMessage, ElMessageBox, ElButton, ElTag, ElTooltip } from 'element-plus'
  import { VueDraggable } from 'vue-draggable-plus'
  import {
    TableConfigModel,
    TableFieldConfigModel,
    SyncTableFieldListParams,
    getSimpleTableFieldParam,
    SimpleTableField,
    TableFieldConfigListParams
  } from '@/api/model/tableConfigModel'
  import { TableService } from '@/api/tableConfigApi'
  import { DictService } from '@/api/dictApi'
  import type { FormInstance, FormRules } from 'element-plus'
  import { useCheckedColumns, ColumnOption } from '@/composables/useCheckedColumns'
  import type { SearchFormItem } from '@/types/search-form'
  import ArtButtonTable from '@/components/core/forms/ArtButtonTable.vue'
  import TablePreview from './TablePreview.vue'
  import { useI18n } from 'vue-i18n'

  const { t } = useI18n()

  const props = defineProps<{
    visible: boolean
    tableConfig?: TableConfigModel
  }>()

  const emit = defineEmits(['update:visible', 'refresh'])

  // 使用内部可变的响应式变量而不是computed
  const internalFieldList = ref<TableFieldConfigModel[]>([])
  // 加载状态
  const fieldListLoading = ref(false)

  // 搜索表单初始值
  const initialSearchState = {
    page: 1,
    limit: 10,
    columnName: '',
    businessName: '',
    formComponentType: '',
    isQueryField: undefined,
    tableConfigId: props.tableConfig?.id || 0
  }

  // 响应式搜索表单数据
  const formFilters = ref<TableFieldConfigListParams & { isQueryField?: string }>({
    ...initialSearchState,
    isQueryField: undefined
  })

  // 分页信息
  const pagination = reactive({
    total: 0
  })

  // 搜索表单项
  const formItems: SearchFormItem[] = [
    {
      label: t('tableFieldConfig.search.fieldName'),
      prop: 'columnName',
      type: 'input',
      elColSpan: 5,
      config: {
        placeholder: t('tableFieldConfig.search.inputFieldName'),
        clearable: true
      }
    },
    {
      label: t('tableFieldConfig.search.businessName'),
      prop: 'businessName',
      type: 'input',
      elColSpan: 5,
      config: {
        placeholder: t('tableFieldConfig.search.inputBusinessName'),
        clearable: true
      }
    },
    {
      label: t('tableFieldConfig.search.componentType'),
      prop: 'formComponentType',
      type: 'select',
      elColSpan: 5,
      config: {
        placeholder: t('tableFieldConfig.search.selectComponentType'),
        clearable: true
      },
      options: () => [
        { label: t('tableFieldConfig.componentType.input'), value: 'INPUT' },
        { label: t('tableFieldConfig.componentType.textarea'), value: 'TEXTAREA' },
        { label: t('tableFieldConfig.componentType.select'), value: 'SELECT' },
        { label: t('tableFieldConfig.componentType.inputNumber'), value: 'INPUT_NUMBER' },
        { label: t('tableFieldConfig.componentType.radio'), value: 'RADIO' },
        { label: t('tableFieldConfig.componentType.checkbox'), value: 'CHECKBOX' },
        { label: t('tableFieldConfig.componentType.switch'), value: 'SWITCH' }
      ]
    },
    {
      label: t('tableFieldConfig.search.isQuery'),
      prop: 'isQueryField',
      type: 'select',
      elColSpan: 5,
      options: () => [
        { label: t('tableFieldConfig.search.yes'), value: '1' },
        { label: t('tableFieldConfig.search.no'), value: '0' }
      ],
      config: {
        placeholder: t('tableFieldConfig.search.selectIsQuery'),
        clearable: true
      }
    }
  ]

  // 表格列配置
  const columnOptions: ColumnOption[] = [
    {
      label: t('tableFieldConfig.column.sort'),
      prop: 'sortable',
      fixed: 'left',
      checked: false,
      formatter: () => {
        return h(
          'div',
          h(ArtButtonTable, {
            title: t('tableFieldConfig.column.sort'),
            icon: '&#xe840;',
            iconColor: '#88970e',
            extraClass: 'handle'
          })
        )
      }
    },
    { label: t('tableFieldConfig.column.id'), prop: 'id', minWidth: 60, fixed: 'left' },
    { label: t('tableFieldConfig.column.sortValue'), prop: 'sort', minWidth: 100 },
    { label: t('tableFieldConfig.column.fieldName'), prop: 'columnName', minWidth: 120 },
    { label: t('tableFieldConfig.column.fieldType'), prop: 'columnType', minWidth: 100 },
    { label: t('tableFieldConfig.column.businessName'), prop: 'businessName', minWidth: 150 },
    {
      label: t('tableFieldConfig.column.componentType'),
      prop: 'formComponentType',
      minWidth: 140,
      formatter: (row) => componentTypeMap[row.formComponentType] || row.formComponentType
    },
    {
      label: t('tableFieldConfig.column.isUnique'),
      prop: 'isUnique',
      minWidth: 100,
      formatter: (row) =>
        h(ElTag, { type: row.isUnique ? 'danger' : 'info' }, () =>
          row.isUnique ? t('tableFieldConfig.column.yes') : t('tableFieldConfig.column.no')
        )
    },
    {
      label: t('tableFieldConfig.column.isRequired'),
      prop: 'isNullable',
      minWidth: 120,
      formatter: (row) =>
        h(ElTag, { type: !row.isNullable ? 'danger' : 'info' }, () =>
          !row.isNullable ? t('tableFieldConfig.column.yes') : t('tableFieldConfig.column.no')
        )
    },
    {
      label: t('tableFieldConfig.column.isQueryField'),
      prop: 'isQueryField',
      minWidth: 120,
      formatter: (row) =>
        h(ElTag, { type: row.isQueryField ? 'success' : 'info' }, () =>
          row.isQueryField ? t('tableFieldConfig.column.yes') : t('tableFieldConfig.column.no')
        )
    },
    {
      label: t('tableFieldConfig.column.isListVisible'),
      prop: 'isListVisible',
      minWidth: 120,
      formatter: (row) =>
        h(ElTag, { type: row.isListVisible ? 'success' : 'info' }, () =>
          row.isListVisible ? t('tableFieldConfig.column.yes') : t('tableFieldConfig.column.no')
        )
    },
    {
      label: t('tableFieldConfig.column.isAddVisible'),
      prop: 'isAddVisible',
      minWidth: 120,
      formatter: (row) =>
        h(ElTag, { type: row.isAddVisible ? 'success' : 'info' }, () =>
          row.isAddVisible ? t('tableFieldConfig.column.yes') : t('tableFieldConfig.column.no')
        )
    },
    {
      label: t('tableFieldConfig.column.isEditVisible'),
      prop: 'isEditVisible',
      minWidth: 120,
      formatter: (row) =>
        h(ElTag, { type: row.isEditVisible ? 'success' : 'info' }, () =>
          row.isEditVisible ? t('tableFieldConfig.column.yes') : t('tableFieldConfig.column.no')
        )
    },
    {
      label: t('tableFieldConfig.column.isDetailVisible'),
      prop: 'isDetailVisible',
      minWidth: 120,
      formatter: (row) =>
        h(ElTag, { type: row.isDetailVisible ? 'success' : 'info' }, () =>
          row.isDetailVisible ? t('tableFieldConfig.column.yes') : t('tableFieldConfig.column.no')
        )
    },
    {
      label: t('tableFieldConfig.column.queryType'),
      prop: 'queryType',
      minWidth: 100,
      formatter: (row) => queryTypeMap[row.queryType] || row.queryType
    },
    {
      label: t('tableFieldConfig.column.associatedType'),
      prop: 'associatedType',
      minWidth: 100,
      formatter: (row) => associatedTypeMap[row.associatedType] || row.associatedType
    },
    { label: t('tableFieldConfig.column.dictType'), prop: 'associatedDictType', minWidth: 100 },
    { label: t('tableFieldConfig.column.associatedTable'), prop: 'associatedTable', minWidth: 100 },
    {
      label: t('tableFieldConfig.column.associatedTableField'),
      prop: 'associatedTableField',
      minWidth: 100
    },
    { label: t('tableFieldConfig.column.validationRules'), prop: 'validationRules', minWidth: 100 },
    { label: t('tableFieldConfig.column.fieldComment'), prop: 'columnComment', minWidth: 150 },
    {
      label: t('tableFieldConfig.column.actions'),
      prop: 'actions',
      fixed: 'right',
      width: 180,
      formatter: (row) => {
        return h('div', [
          h(ArtButtonTable, {
            title: t('tableConfig.button.edit'),
            type: 'edit',
            auth: 'tableconfig_edit_field',
            onClick: () => handleEditField(row)
          }),
          h(ArtButtonTable, {
            title: t('tableConfig.button.delete'),
            type: 'delete',
            auth: 'tableconfig_field_delete',
            onClick: () => handleDeleteField(row)
          })
        ])
      }
    }
  ]

  // 使用 Hook 获取响应式的列配置和选中状态
  const { columns, columnChecks } = useCheckedColumns(() => columnOptions)

  // 添加加载字段数据的方法
  const loadFieldData = async () => {
    if (!props.tableConfig?.id) {
      ElMessage.warning(t('tableFieldConfig.warning.missingTableConfig'))
      return
    }

    fieldListLoading.value = true
    try {
      const params: TableFieldConfigListParams = {
        ...formFilters.value,
        isQueryField:
          formFilters.value.isQueryField === '1'
            ? true
            : formFilters.value.isQueryField === '0'
              ? false
              : undefined,
        tableConfigId: props.tableConfig.id
      }

      const res = await TableService.getTableFieldConfigList(params)
      if (res.success && res.data) {
        internalFieldList.value = res.data.records
        pagination.total = res.data.total
      } else {
        ElMessage.error(res.message || t('tableFieldConfig.message.loadFieldsFailed'))
      }
    } catch (error) {
      console.error(t('tableFieldConfig.message.loadFieldsFailed') + ':', error)
      ElMessage.error(t('tableFieldConfig.message.loadFieldsError'))
    } finally {
      fieldListLoading.value = false
    }
  }

  // 搜索方法
  const handleSearch = () => {
    formFilters.value.page = 1 // 搜索时重置为第一页
    loadFieldData()
  }

  // 重置搜索
  const handleReset = () => {
    Object.assign(formFilters.value, initialSearchState)
    loadFieldData()
  }

  // 处理分页变化
  const handleCurrentChange = (page: number) => {
    formFilters.value.page = page
    loadFieldData()
  }

  // 处理每页条数变化
  const handleSizeChange = (size: number) => {
    formFilters.value.limit = size
    formFilters.value.page = 1 // 切换每页数量时重置为第一页
    loadFieldData()
  }

  // 添加排序模式状态变量
  const sortMode = ref(false)

  // 用于存储排序前的字段列表，以便取消排序时恢复
  const originalFieldListBackup = ref<TableFieldConfigModel[]>([])

  // 处理关闭对话框
  const handleClose = () => {
    emit('update:visible', false)
  }

  // 提交加载状态
  const submitLoading = ref(false)

  // 字段表单对话框
  const fieldDialogVisible = ref(false)
  const fieldDialogTitle = ref('编辑字段')
  const fieldFormRef = ref<FormInstance>()

  // 保存原始字段数据，用于【是否必填】字段的校验
  const originalFieldData = ref<TableFieldConfigModel | null>(null)

  // 字典类型列表
  const dictTypeList = ref<any[]>([])
  // 关联表列表
  const associatedTableList = ref<string[]>([])
  // 关联表字段列表
  const associatedTableFields = ref<SimpleTableField[]>([])

  // 字段信息（只读或从行数据中获取的字段）
  const fieldInfo = reactive({
    columnName: '',
    columnType: '',
    columnComment: '',
    javaType: '',
    javaFieldName: '',
    tableConfigId: 0,
    schemaName: '',
    tableName: ''
  })

  // 字段表单数据（实际提交的字段）
  const fieldForm = reactive<any>({
    id: 0,
    businessName: '',
    formComponentType: '',
    dictType: '',
    validationRules: '',
    isUnique: false,
    isNullable: true,
    isQueryField: false,
    isListVisible: true,
    isAddVisible: true,
    isEditVisible: true,
    isDetailVisible: true,
    queryType: 'eq',
    sort: 0,
    remarks: '',
    // 新增字段
    associatedType: null, // 1: 字典类型, 2: 关联表名
    associatedDictType: '',
    associatedTable: '',
    associatedTableField: ''
  })

  // 判断关联类型是否必填
  const isAssociatedTypeRequired = computed(() => {
    return ['SELECT', 'RADIO', 'CHECKBOX'].includes(fieldForm.formComponentType)
  })

  // 字段表单验证规则
  const fieldFormRules = reactive<FormRules>({
    formComponentType: [
      {
        required: false,
        message: t('tableFieldConfig.validation.componentTypeRequired'),
        trigger: 'change'
      }
    ],
    associatedType: [
      {
        required: false,
        message: t('tableFieldConfig.validation.associatedTypeRequired'),
        trigger: 'change'
      }
    ],
    associatedDictType: [
      {
        required: false, // 初始设为false，在运行时动态检查
        message: t('tableFieldConfig.validation.dictTypeRequired'),
        trigger: 'change'
      }
    ],
    associatedTable: [
      {
        required: false, // 初始设为false，在运行时动态检查
        message: t('tableFieldConfig.validation.associatedTableRequired'),
        trigger: 'change'
      }
    ],
    associatedTableField: [
      {
        required: false,
        message: t('tableFieldConfig.validation.displayFieldRequired'),
        trigger: 'change'
      }
    ]
  })

  // 获取字典类型列表
  const loadDictTypeList = async () => {
    try {
      const res = await DictService.getDictTypeList()
      if (res.success && res.data) {
        dictTypeList.value = res.data as any
      }
    } catch (error) {
      console.error('获取字典类型列表失败:', error)
    }
  }

  // 获取关联表列表
  const loadAssociatedTableList = async (schemaName: string) => {
    if (!schemaName) {
      ElMessage.warning(t('tableFieldConfig.warning.missingDBName'))
      return
    }

    try {
      const res = await TableService.getTableNameList({ schemaName })
      if (res.success && res.data) {
        associatedTableList.value = res.data
      }
    } catch (error) {
      console.error(t('tableFieldConfig.message.loadFieldsFailed') + ':', error)
    }
  }

  // 获取关联表字段列表
  const loadAssociatedTableFields = async () => {
    try {
      const fieldParams: getSimpleTableFieldParam = {
        tableName: fieldForm.associatedTable || '',
        schemaName: props.tableConfig?.schemaName || ''
      }
      const res = await TableService.getSimpleTableField(fieldParams)
      if (res.success && res.data) {
        associatedTableFields.value = Array.isArray(res.data) ? res.data : [res.data]
      }
    } catch (error) {
      console.error('获取表字段列表失败:', error)
    }
  }

  // 关联类型变化处理
  const handleAssociatedTypeChange = (val: number) => {
    // 重置相关字段
    if (val === 1) {
      fieldForm.associatedTable = ''
      fieldForm.associatedTableField = ''

      // 加载字典类型列表
      if (dictTypeList.value.length === 0) {
        loadDictTypeList()
      }
    } else if (val === 2) {
      fieldForm.associatedDictType = ''

      // 加载关联表列表
      if (fieldInfo.schemaName && associatedTableList.value.length === 0) {
        loadAssociatedTableList(fieldInfo.schemaName)
      }
    }
  }

  // 字典类型变化处理
  const handleDictTypeChange = (val: string) => {
    if (val) {
      // 可以在这里做一些字典类型相关的逻辑
    }
  }

  // 关联表名变化处理
  const handleTableNameChange = async (val: string) => {
    // 清空关联表字段
    fieldForm.associatedTableField = ''

    if (val && fieldInfo.tableConfigId) {
      // 加载关联表字段列表
      await loadAssociatedTableFields()
    }
  }

  // 重置字段表单和信息
  const resetFieldForm = () => {
    // 重置原始数据
    originalFieldData.value = null

    // 重置字段信息
    fieldInfo.columnName = ''
    fieldInfo.columnType = ''
    fieldInfo.columnComment = ''
    fieldInfo.javaType = ''
    fieldInfo.javaFieldName = ''
    fieldInfo.tableConfigId = 0
    fieldInfo.schemaName = ''

    // 重置字段表单
    fieldForm.id = 0
    fieldForm.businessName = ''
    fieldForm.formComponentType = ''
    fieldForm.dictType = ''
    fieldForm.validationRules = ''
    fieldForm.isUnique = false
    fieldForm.isNullable = true
    fieldForm.isQueryField = false
    fieldForm.isListVisible = true
    fieldForm.isAddVisible = true
    fieldForm.isEditVisible = true
    fieldForm.isDetailVisible = true
    fieldForm.queryType = 'eq'
    fieldForm.sort = 0
    fieldForm.remarks = ''
    // 重置新增字段
    fieldForm.associatedType = null
    fieldForm.associatedDictType = ''
    fieldForm.associatedTable = ''
    fieldForm.associatedTableField = ''

    // 重置列表数据
    dictTypeList.value = []
    associatedTableList.value = []
    associatedTableFields.value = []
  }

  // 同步字段
  const handleSyncFields = async () => {
    if (!props.tableConfig?.id) {
      ElMessage.warning(t('tableFieldConfig.warning.missingTableConfig'))
      return
    }

    try {
      // 构造正确的参数
      const params: SyncTableFieldListParams = {
        tableName: props.tableConfig.tableName,
        schemaName: props.tableConfig.schemaName
      }
      const res = await TableService.syncFieldsByTableName(params)
      if (res.success) {
        ElMessage.success(t('tableFieldConfig.message.syncSuccess'))
        // 刷新本组件字段列表
        await loadFieldData()
        // 刷新父组件数据
        emit('refresh')
      } else {
        ElMessage.error(res.message || t('tableFieldConfig.message.syncFailed'))
      }
    } catch (error) {
      console.error(t('tableFieldConfig.message.syncError') + ':', error)
      ElMessage.error(t('tableFieldConfig.message.syncError'))
    }
  }

  // 修改字段排序方法
  const handleFieldSort = () => {
    // 切换排序模式
    sortMode.value = !sortMode.value

    if (sortMode.value) {
      columnChecks.value[0].checked = true
      // 进入排序模式，备份当前字段列表
      originalFieldListBackup.value = JSON.parse(JSON.stringify(internalFieldList.value))
      ElMessage.info(t('tableFieldConfig.message.enterSortMode'))
    } else {
      columnChecks.value[0].checked = false
      // 如果是退出排序模式，恢复原始数据（取消排序）
      // 这里不需要调用API，所以直接恢复备份的数据
      internalFieldList.value = [...originalFieldListBackup.value]
      loadFieldData() // 刷新字段列表数据
      emit('refresh')
    }
  }

  // 保存排序方法
  const handleSaveSortOrder = async () => {
    if (!props.tableConfig?.id) {
      ElMessage.warning(t('tableFieldConfig.warning.missingTableConfig'))
      return
    }

    try {
      // 获取排序后的字段ID列表
      const sortedIds = internalFieldList.value.map((field: TableFieldConfigModel) => field.id)

      // 调用排序API
      const res = await TableService.fieldSort({ ids: sortedIds })
      if (res.success) {
        ElMessage.success(t('tableFieldConfig.message.sortSuccess'))
        sortMode.value = false
        columnChecks.value[0].checked = false
        // 刷新本组件字段列表
        await loadFieldData()
        // 刷新父组件数据
        emit('refresh')
      } else {
        ElMessage.error(res.message || t('tableFieldConfig.message.sortFailed'))
      }
    } catch (error) {
      console.error(t('tableFieldConfig.message.sortError') + ':', error)
      ElMessage.error(t('tableFieldConfig.message.sortError'))
    }
  }

  // 添加取消排序方法
  const handleCancelSort = () => {
    // 恢复原始字段列表
    if (sortMode.value) {
      columnChecks.value[0].checked = false
      internalFieldList.value = [...originalFieldListBackup.value]
      ElMessage.info(t('tableFieldConfig.message.cancelSort'))
      sortMode.value = false
      // 刷新字段列表数据
      loadFieldData()
      // 恢复备份的数据
      emit('refresh')
    }
  }

  // 编辑字段
  const handleEditField = (field: TableFieldConfigModel) => {
    fieldDialogTitle.value = t('tableFieldConfig.dialog.editFieldTitle', {
      fieldName: field.columnName
    })

    // 保存原始字段数据，用于【是否必填】字段的校验
    originalFieldData.value = { ...field }

    // 设置字段信息（从行数据中获取）
    fieldInfo.columnName = field.columnName
    fieldInfo.columnType = field.columnType
    fieldInfo.columnComment = field.columnComment || ''
    fieldInfo.javaType = field.javaType
    fieldInfo.javaFieldName = field.javaFieldName
    fieldInfo.tableConfigId = field.tableConfigId
    fieldInfo.schemaName = field.schemaName

    // 设置字段表单数据（实际需要编辑的内容）
    fieldForm.id = field.id
    fieldForm.businessName = field.businessName || ''
    fieldForm.formComponentType = field.formComponentType || ''
    fieldForm.dictType = field.dictType || ''
    fieldForm.validationRules = field.validationRules || ''
    fieldForm.isUnique = field.isUnique || false
    fieldForm.isNullable = field.isNullable ?? true
    fieldForm.isQueryField = field.isQueryField || false
    fieldForm.isListVisible = field.isListVisible ?? true
    fieldForm.isAddVisible = field.isAddVisible ?? true
    fieldForm.isEditVisible = field.isEditVisible ?? true
    fieldForm.isDetailVisible = field.isDetailVisible ?? true
    if (field.isQueryField) {
      fieldForm.queryType = field.queryType || ''
    } else {
      fieldForm.queryType = ''
    }
    fieldForm.sort = field.sort || 0
    fieldForm.remarks = field.remarks || ''

    // 设置新增字段的值
    fieldForm.associatedType = field.associatedType || null
    fieldForm.associatedDictType = field.associatedDictType || ''
    fieldForm.associatedTable = field.associatedTable || ''
    fieldForm.associatedTableField = field.associatedTableField || ''

    // 根据关联类型加载对应的数据
    if (fieldForm.associatedType === 1) {
      loadDictTypeList()
    } else if (fieldForm.associatedType === 2 && fieldInfo.schemaName) {
      loadAssociatedTableList(fieldInfo.schemaName)
      if (fieldForm.associatedTable && fieldInfo.tableConfigId) {
        loadAssociatedTableFields()
      }
    }

    fieldDialogVisible.value = true
  }

  // 提交字段表单
  const submitFieldForm = async () => {
    if (!fieldFormRef.value) return

    // 在表单验证前进行额外验证
    if (isAssociatedTypeRequired.value && !fieldForm.associatedType) {
      ElMessage.error(t('tableFieldConfig.warning.associatedTypeRequired'))
      return
    }

    // 增加对关联子项的验证
    if (
      isAssociatedTypeRequired.value &&
      fieldForm.associatedType === 1 &&
      !fieldForm.associatedDictType
    ) {
      ElMessage.error(t('tableFieldConfig.warning.selectDictType'))
      return
    }

    if (
      isAssociatedTypeRequired.value &&
      fieldForm.associatedType === 2 &&
      !fieldForm.associatedTable
    ) {
      ElMessage.error(t('tableFieldConfig.warning.selectAssociatedTable'))
      return
    }

    await fieldFormRef.value.validate(async (valid) => {
      if (valid) {
        submitLoading.value = true
        try {
          // 构造编辑字段参数，确保符合EditTableFieldConfigParams类型
          const params: any = {
            ...fieldForm,
            tableId: props.tableConfig?.id || 0,
            // 使用fieldInfo中的只读数据
            columnName: fieldInfo.columnName,
            columnType: fieldInfo.columnType,
            columnComment: fieldInfo.columnComment,
            javaType: fieldInfo.javaType,
            javaFieldName: fieldInfo.javaFieldName,
            tableConfigId: fieldInfo.tableConfigId
          }

          const res = await TableService.editTableFieldConfig(params)
          if (res.success) {
            ElMessage.success(t('tableFieldConfig.message.saveSuccess'))
            fieldDialogVisible.value = false

            // 刷新本组件字段列表
            await loadFieldData()
            // 刷新父组件数据
            emit('refresh')
          } else {
            ElMessage.error(res.message || t('tableFieldConfig.message.saveFailed'))
          }
        } catch (error) {
          console.error(t('tableFieldConfig.message.saveError') + ':', error)
          ElMessage.error(t('tableFieldConfig.message.saveError'))
        } finally {
          submitLoading.value = false
        }
      }
    })
  }

  // 监听父组件传递的tableConfig变化
  watch(
    () => props.tableConfig,
    (newVal) => {
      // 当tableConfig变化且有有效ID时，加载字段列表
      if (newVal && newVal.id) {
        loadFieldData()
      }
    },
    { deep: true }
  )

  // 组件挂载时初始化
  onMounted(() => {
    // 预加载字典类型列表
    loadDictTypeList()

    // 如果有tableConfig，则加载字段列表
    if (props.tableConfig?.id) {
      loadFieldData()
    }
  })

  // 预览整个表单
  const handlePreviewTable = () => {
    if (!props.tableConfig) {
      ElMessage.warning(t('tableFieldConfig.warning.missingTableConfigInfo'))
      return
    }

    // 显示预览对话框
    previewVisible.value = true
  }

  // 添加预览对话框可见性控制变量
  const previewVisible = ref(false)

  // 删除字段
  const handleDeleteField = (field: TableFieldConfigModel) => {
    ElMessageBox.confirm(
      t('tableFieldConfig.message.deleteConfirm', { fieldName: field.columnName }),
      t('common.warning'),
      {
        confirmButtonText: t('tableFieldConfig.button.confirm'),
        cancelButtonText: t('tableFieldConfig.button.cancel'),
        type: 'warning'
      }
    )
      .then(async () => {
        const res = await TableService.deleteTableFieldConfig({ id: field.id })
        if (res.success) {
          ElMessage.success(t('tableFieldConfig.message.deleteSuccess'))
          // 删除成功后刷新本组件字段列表
          await loadFieldData()
          // 刷新父组件数据
          emit('refresh')
        } else {
          ElMessage.error(res.message || t('tableFieldConfig.message.deleteFailed'))
        }
      })
      .catch(() => {
        // 用户取消操作
      })
  }

  // 添加对formComponentType的监听
  // 监听组件类型变化，当选择特定组件时关联类型为必填
  watch(
    () => fieldForm.formComponentType,
    (newVal) => {
      if (['SELECT', 'RADIO', 'CHECKBOX'].includes(newVal)) {
        // 如果切换到需要关联类型的组件，但当前未设置关联类型，给出提示
        if (!fieldForm.associatedType) {
          ElMessage.warning(t('tableFieldConfig.warning.componentTypeRequireAssociated'))
        }
      }
    }
  )

  // 组件类型映射
  const componentTypeMap: Record<string, string> = {
    INPUT: t('tableFieldConfig.componentType.input'),
    TEXTAREA: t('tableFieldConfig.componentType.textarea'),
    SELECT: t('tableFieldConfig.componentType.select'),
    INPUT_NUMBER: t('tableFieldConfig.componentType.inputNumber'),
    RADIO: t('tableFieldConfig.componentType.radio'),
    CHECKBOX: t('tableFieldConfig.componentType.checkbox'),
    COLOR_PICKER: t('tableFieldConfig.componentType.colorPicker'),
    ICON_PICKER: t('tableFieldConfig.componentType.iconPicker'),
    DATE_PICKER: t('tableFieldConfig.componentType.datePicker'),
    TIME_PICKER: t('tableFieldConfig.componentType.timePicker'),
    DATETIME_PICKER: t('tableFieldConfig.componentType.datetimePicker'),
    SWITCH: t('tableFieldConfig.componentType.switch'),
    IMAGE_UPLOAD: t('tableFieldConfig.componentType.imageUpload'),
    FILE_UPLOAD: t('tableFieldConfig.componentType.fileUpload'),
    RICH_TEXT: t('tableFieldConfig.componentType.richText')
  }

  // 查询方式映射
  const queryTypeMap: Record<string, string> = {
    eq: t('tableFieldConfig.queryType.eq'),
    ne: t('tableFieldConfig.queryType.ne'),
    gt: t('tableFieldConfig.queryType.gt'),
    ge: t('tableFieldConfig.queryType.ge'),
    lt: t('tableFieldConfig.queryType.lt'),
    le: t('tableFieldConfig.queryType.le'),
    like: t('tableFieldConfig.queryType.like'),
    left_like: t('tableFieldConfig.queryType.leftLike'),
    right_like: t('tableFieldConfig.queryType.rightLike'),
    between: t('tableFieldConfig.queryType.between'),
    in: t('tableFieldConfig.queryType.in'),
    not_in: t('tableFieldConfig.queryType.notIn'),
    is_not_null: t('tableFieldConfig.queryType.isNotNull'),
    is_null: t('tableFieldConfig.queryType.isNull')
  }

  // 关联类型映射
  const associatedTypeMap: Record<number, string> = {
    1: t('tableFieldConfig.associatedType.dictType'),
    2: t('tableFieldConfig.associatedType.associatedTable')
  }
</script>

<style lang="scss" scoped>
  .draggable-container {
    display: flex;
    flex: 1;
    flex-direction: column;
    height: 100%;
    overflow: hidden;
  }

  .field-config-container {
    :deep(.el-dialog) {
      margin-top: 2vh !important;
    }

    :deep(.el-divider) {
      margin: 16px 0;
    }
  }

  :deep(.el-form-item__label) {
    font-weight: normal;
  }

  :deep(.el-dialog__body) {
    padding: 15px 20px;
  }

  :deep(.el-form-item) {
    margin-bottom: 15px;
  }

  :deep(.el-switch) {
    margin-left: 0;
  }

  :deep(.el-form--label-right .el-form-item__label) {
    padding-right: 8px;
  }

  .warning-text {
    display: inline-block;
    padding-top: 4px;
    font-size: 12px;
    line-height: 1;
    color: #e6a23c;
  }

  .el-form-item-msg {
    margin-top: 4px;
  }

  .art-table-card {
    display: flex;
    flex: 1;
    flex-direction: column;
    overflow: hidden;

    :deep(.el-card__body) {
      display: flex;
      flex-direction: column;
      height: 100%;
      padding: 15px;
    }
  }
</style>

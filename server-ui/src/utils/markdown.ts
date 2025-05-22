// src/plugins/markdown.ts
import MarkdownIt from 'markdown-it'
import hljs from 'highlight.js'

export const md = new MarkdownIt({
  html: false, // 不允许HTML标签
  xhtmlOut: false, // 使用'/'闭合单标签
  breaks: true, // 将\n转换为<br>
  linkify: true, // 将URL字符串转换为链接
  typographer: true, // 启用一些语言中立的替换和引号美化
  highlight: function (str, lang) {
    if (lang && hljs.getLanguage(lang)) {
      try {
        return hljs.highlight(str, { language: lang }).value
      } catch (__) {
        console.error('highlight error', __)
      }
    }
    return '' // 使用外部默认转义
  }
})

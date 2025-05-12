/**
 * 日期格式化工具函数
 */

/**
 * 格式化日期为指定格式
 * @param dateStr 日期字符串或日期对象
 * @param format 格式化模式，默认为 'yyyy-MM-dd hh:mm:ss'
 * @returns 格式化后的日期字符串，如果日期无效则返回 '-'
 */
export function formatDate(dateStr: string | Date, format: string = 'yyyy-MM-dd hh:mm:ss'): string {
  if (!dateStr) return '-'

  try {
    const date = typeof dateStr === 'string' ? new Date(dateStr) : dateStr

    // 检查日期是否有效
    if (isNaN(date.getTime())) return '-'

    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    const hours = String(date.getHours()).padStart(2, '0')
    const minutes = String(date.getMinutes()).padStart(2, '0')
    const seconds = String(date.getSeconds()).padStart(2, '0')

    return format
      .replace('yyyy', String(year))
      .replace('MM', month)
      .replace('dd', day)
      .replace('hh', hours)
      .replace('mm', minutes)
      .replace('ss', seconds)
  } catch (error) {
    console.error('格式化日期失败:', error)
    return typeof dateStr === 'string' ? dateStr : '-'
  }
}

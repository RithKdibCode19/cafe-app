export const useTrans = () => {
  const { locale } = useI18n()

  const trans = (obj: any, field: string) => {
    if (!obj) return ''
    
    // If locale is Khmer, try for _kh field
    if (locale.value === 'km') {
      const khFieldName = `${field}Kh`
      if (obj[khFieldName]) {
        return obj[khFieldName]
      }
    }

    // Default to the original field (English)
    return obj[field] || ''
  }

  return {
    trans
  }
}

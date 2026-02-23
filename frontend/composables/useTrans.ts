export const useTrans = () => {
  const { locale } = useI18n();

  /**
   * Translates a field of an entity based on the current locale.
   * Checks for a field with the suffix 'Kh' if the locale is 'km' (Khmer).
   * Falls back to the base field if the translated field is missing or empty.
   * 
   * @param entity The entity object containing the data.
   * @param field The base field name (e.g., 'name', 'description').
   * @returns The translated string or the base string.
   */
  const tr = (entity: any, field: string): string => {
    if (!entity) return '';

    if (locale.value === 'km') {
      const fieldKh = `${field}Kh`;
      if (entity[fieldKh] && entity[fieldKh].trim() !== '') {
        return entity[fieldKh];
      }
    }
    
    return entity[field] || '';
  };

  return {
    tr
  };
};

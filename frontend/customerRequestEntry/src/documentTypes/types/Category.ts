export enum Category {
  SUGGESTION_FOR_IMPROVEMENT = 'SUGGESTION_FOR_IMPROVEMENT',
  BUG_REPORT = 'BUG_REPORT',
  FEATURE_REQUEST = 'FEATURE_REQUEST',
  TRAINING_REQUEST = 'TRAINING_REQUEST',
  OTHER = 'OTHER',
}

export const CategoryLabel: Record<Category, string> = {
  [Category.SUGGESTION_FOR_IMPROVEMENT]: 'Verbesserungsvorschlag',
  [Category.BUG_REPORT]: 'Fehlerbericht',
  [Category.FEATURE_REQUEST]: 'Funktionsanfrage',
  [Category.TRAINING_REQUEST]: 'Schulungsanfrage',
  [Category.OTHER]: 'Anderes',
}

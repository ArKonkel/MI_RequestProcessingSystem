export enum ExpertiseLevel {
  BEGINNER = "BEGINNER",
  INTERMEDIATE = "INTERMEDIATE",
  ADVANCED = "ADVANCED",
  EXPERT = "EXPERT",
}

export const ExpertiseLevelLabel: Record<ExpertiseLevel, string> = {
  [ExpertiseLevel.BEGINNER]: 'Anfänger',
  [ExpertiseLevel.INTERMEDIATE]: 'Mittel',
  [ExpertiseLevel.ADVANCED]: 'Fortgeschritten',
  [ExpertiseLevel.EXPERT]: 'Experte',
}

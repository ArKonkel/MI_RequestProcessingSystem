export enum TimeUnit {
  MINUTES = 'MINUTES',
  HOUR = 'HOUR',
  DAY = 'DAY',
  WEEK = 'WEEK',
  MONTH = 'MONTH',
  YEAR = 'YEAR',
}

export const TimeUnitLabel: Record<TimeUnit, string> = {
  [TimeUnit.MINUTES]: 'Minuten',
  [TimeUnit.HOUR]: 'Stunden',
  [TimeUnit.DAY]: 'Tage',
  [TimeUnit.WEEK]: 'Wochen',
  [TimeUnit.MONTH]: 'Monate',
  [TimeUnit.YEAR]: 'Jahre',
}

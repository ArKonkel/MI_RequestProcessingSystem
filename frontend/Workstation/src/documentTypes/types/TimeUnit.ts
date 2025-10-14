export enum TimeUnit {
  MINUTES = 'MINUTES',
  HOUR = 'HOUR',
  DAY = 'DAY',
  WEEK = 'WEEK',
  MONTH = 'MONTH',
  YEAR = 'YEAR',
}

export const TimeUnitLabel: Record<TimeUnit, string> = {
  [TimeUnit.MINUTES]: 'min',
  [TimeUnit.HOUR]: 'h',
  [TimeUnit.DAY]: 't',
  [TimeUnit.WEEK]: 'w',
  [TimeUnit.MONTH]: 'mnt',
  [TimeUnit.YEAR]: 'year',
}

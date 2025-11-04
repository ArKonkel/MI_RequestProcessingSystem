export enum TimeUnit {
  MINUTES = 'MINUTES',
  HOUR = 'HOUR',
  DAY = 'DAY',
}

export const TimeUnitLabel: Record<TimeUnit, string> = {
  [TimeUnit.MINUTES]: 'min',
  [TimeUnit.HOUR]: 'h',
  [TimeUnit.DAY]: 't',
}

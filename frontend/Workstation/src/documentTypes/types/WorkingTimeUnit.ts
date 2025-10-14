
export enum WorkingTimeUnit {
  MINUTES = 'MINUTES', HOURS = 'HOURS',
}


export const WorkingTimeUnitlabel: Record<WorkingTimeUnit, string> = {
  [WorkingTimeUnit.MINUTES]: 'Minuten',
  [WorkingTimeUnit.HOURS]: 'Stunden',
}

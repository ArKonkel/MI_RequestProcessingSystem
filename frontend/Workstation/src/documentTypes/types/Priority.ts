export enum Priority {
  LOW = 'LOW',
  MEDIUM = 'MEDIUM',
  HIGH = 'HIGH',
}

export const PriorityLabel: Record<Priority, string> = {
  [Priority.LOW]: 'Niedrig',
  [Priority.MEDIUM]: 'Mittel',
  [Priority.HIGH]: 'Wichtig',
}

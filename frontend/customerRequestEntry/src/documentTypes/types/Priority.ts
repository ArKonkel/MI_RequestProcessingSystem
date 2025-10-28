export enum Priority {
  LOW = 'LOW',
  MEDIUM = 'MEDIUM',
  HIGH = 'HIGH',
}

export const PriorityLabel: Record<Priority, string> = {
  [Priority.LOW]: 'Niedrig',
  [Priority.MEDIUM]: 'Mittel',
  [Priority.HIGH]: 'Hoch',
}

export function getPriorityColor(priority: Priority) {
  switch (priority) {
    case Priority.HIGH:
      return "destructive";
    case Priority.MEDIUM:
      return "default";
    case Priority.LOW:
      return "secondary";
    default:
      return "outline";
  }
}

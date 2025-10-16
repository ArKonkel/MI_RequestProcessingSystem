export enum TaskStatus {
  OPEN = 'OPEN',
  IN_PROGRESS = 'IN_PROGRESS',
  BLOCKED = 'BLOCKED',
  IN_REVIEW = 'IN_REVIEW',
  TO_TEST = 'TO_TEST',
  CLOSED = 'CLOSED',
}

export const TaskStatusLabel: Record<TaskStatus, string> = {
  [TaskStatus.OPEN]: 'Offen',
  [TaskStatus.IN_PROGRESS]: 'In Bearbeitung',
  [TaskStatus.BLOCKED]: 'Blockiert',
  [TaskStatus.IN_REVIEW]: 'In Review',
  [TaskStatus.TO_TEST]: 'Zu testen',
  [TaskStatus.CLOSED]: 'Fertig',
}

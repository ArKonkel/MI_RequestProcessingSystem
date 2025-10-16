export enum ProjectStatus {
  CREATED = 'CREATED',
  IN_PLANNING = 'IN_PLANNING',
  READY = 'READY',
  IN_PROGRESS = 'IN_PROGRESS',
  PAUSED = 'PAUSED',
  ABORTED = 'ABORTED',
  FINISHED = 'FINISHED',
}

export const ProjectStatusLabel: Record<ProjectStatus, string> = {
  [ProjectStatus.CREATED]: 'Erstellt',
  [ProjectStatus.IN_PLANNING]: 'In Planung',
  [ProjectStatus.READY]: 'Bereit',
  [ProjectStatus.IN_PROGRESS]: 'In Bearbeitung',
  [ProjectStatus.PAUSED]: 'Pausiert',
  [ProjectStatus.ABORTED]: 'Abgebrochen',
  [ProjectStatus.FINISHED]: 'Fertig',
}

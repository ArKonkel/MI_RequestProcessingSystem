export enum ProjectDependencyType {
  FINISH_TO_START = 'FINISH_TO_START',
  START_TO_START = 'START_TO_START',
  FINISH_TO_FINISH = 'FINISH_TO_FINISH',
  PART_OF = 'PART_OF',
  USES_RESULT = 'USES_RESULT',
}


export const ProjectDependencyTypeLabel: Record<ProjectDependencyType, string> = {
  [ProjectDependencyType.FINISH_TO_START]: 'Fertigstellung bis Beginn',
  [ProjectDependencyType.START_TO_START]: 'Beginn bis Beginn',
  [ProjectDependencyType.FINISH_TO_FINISH]: 'Fertigstellung bis Abschluss',
  [ProjectDependencyType.PART_OF]: 'Teil von',
  [ProjectDependencyType.USES_RESULT]: 'Verwendet Ergebnis',
}

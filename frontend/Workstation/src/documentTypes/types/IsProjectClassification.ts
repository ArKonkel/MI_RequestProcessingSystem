export enum IsProjectClassification {
  YES = 'YES',
  NO = 'NO',
  NOT_DETERMINED = 'NOT_DETERMINED',
}

export const IsProjectClassificationLabel: Record<IsProjectClassification, string> = {
  [IsProjectClassification.NOT_DETERMINED]: 'Nicht bestimmt',
  [IsProjectClassification.YES]: 'Ja',
  [IsProjectClassification.NO]: 'Nein',
}

export enum Chargeable {
  NOT_DEFINED = 'NOT_DEFINED',
  YES = 'YES',
  NO = 'NO',
}

export const ChargeableLabel: Record<Chargeable, string> = {
  [Chargeable.NOT_DEFINED]: 'Nicht geklärt',
  [Chargeable.YES]: 'Ja',
  [Chargeable.NO]: 'Nein',
}

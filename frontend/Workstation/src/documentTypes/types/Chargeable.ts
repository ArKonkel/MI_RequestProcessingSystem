export enum Chargeable {
  NOT_DETERMINED = 'NOT_DETERMINED',
  YES = 'YES',
  NO = 'NO',
}

export const ChargeableLabel: Record<Chargeable, string> = {
  [Chargeable.NOT_DETERMINED]: 'Nicht geklärt',
  [Chargeable.YES]: 'Ja',
  [Chargeable.NO]: 'Nein',
}

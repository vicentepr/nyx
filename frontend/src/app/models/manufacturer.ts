export interface Manufacturer {
  id: number;
  name: string;
  country: string;
  foundedAt: string;
}

export interface ManufacturerFilter {
  name?: string;
  country?: string;
}

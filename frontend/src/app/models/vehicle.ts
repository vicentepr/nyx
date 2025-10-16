import { Manufacturer } from './manufacturer';

export type FuelType = 'GASOLINE' | 'ETHANOL' | 'FLEX' | 'ELECTRIC' | 'HYBRID' | 'DIESEL';

export interface Vehicle {
  id: number;
  model: string;
  licensePlate: string;
  year: number;
  price: number;
  mileage: number;
  color: string;
  fuelType: FuelType;
  manufacturer: Manufacturer;
  createdAt: string;
  updatedAt: string;
}

export interface VehiclePayload {
  model: string;
  licensePlate: string;
  year: number;
  price: number;
  mileage: number;
  color: string;
  fuelType: FuelType;
  manufacturerId: number;
}

export interface VehicleFilter {
  model?: string;
  licensePlate?: string;
  yearFrom?: number;
  yearTo?: number;
  priceFrom?: number;
  priceTo?: number;
  manufacturerId?: number;
  fuelType?: FuelType;
}

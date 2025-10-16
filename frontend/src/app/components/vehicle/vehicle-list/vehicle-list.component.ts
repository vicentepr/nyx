import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { AsyncPipe, CurrencyPipe, DatePipe, NgFor, NgIf } from '@angular/common';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import Swal from 'sweetalert2';

import { VehicleService } from '@services/vehicle/vehicle.service';
import { ManufacturerService } from '@services/manufacturer/manufacturer.service';
import { Vehicle, VehicleFilter } from '@models/vehicle';
import { Manufacturer } from '@models/manufacturer';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-vehicle-list',
  standalone: true,
  imports: [NgFor, NgIf, ReactiveFormsModule, CurrencyPipe, DatePipe, AsyncPipe, RouterLink],
  templateUrl: './vehicle-list.component.html',
  styleUrls: ['./vehicle-list.component.scss']
})
export class VehicleListComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly vehicleService = inject(VehicleService);
  private readonly manufacturerService = inject(ManufacturerService);

  protected readonly filterForm = this.fb.nonNullable.group({
    model: [''],
    licensePlate: [''],
    yearFrom: [null as number | null],
    yearTo: [null as number | null],
    priceFrom: [null as number | null],
    priceTo: [null as number | null],
    manufacturerId: [null as number | null],
    fuelType: ['']
  });

  protected readonly vehicles = signal<Vehicle[]>([]);
  protected readonly manufacturers = signal<Manufacturer[]>([]);
  protected readonly loading = signal(false);

  readonly totalVehicles = computed(() => this.vehicles().length);

  ngOnInit(): void {
    this.loadVehicles();
    this.loadManufacturers();
  }

  loadVehicles(): void {
    this.loading.set(true);
    const filter = this.filterForm.getRawValue() as VehicleFilter;
    this.vehicleService.list(filter).subscribe({
      next: (vehicles) => {
        this.vehicles.set(vehicles);
        this.loading.set(false);
      },
      error: (error) => {
        this.loading.set(false);
        Swal.fire({
          icon: 'error',
          title: 'Erro ao carregar veículos',
          text: error?.error ?? 'Ocorreu um erro inesperado.'
        });
      }
    });
  }

  loadManufacturers(): void {
    this.manufacturerService.list().subscribe({
      next: (manufacturers) => this.manufacturers.set(manufacturers),
      error: (error) => {
        Swal.fire({
          icon: 'error',
          title: 'Erro ao carregar fabricantes',
          text: error?.error ?? 'Ocorreu um erro inesperado.'
        });
      }
    });
  }

  onSearch(): void {
    this.loadVehicles();
  }

  onReset(): void {
    this.filterForm.reset({
      model: '',
      licensePlate: '',
      yearFrom: null,
      yearTo: null,
      priceFrom: null,
      priceTo: null,
      manufacturerId: null,
      fuelType: ''
    });
    this.loadVehicles();
  }

  onDelete(vehicle: Vehicle): void {
    Swal.fire({
      icon: 'warning',
      title: 'Remover veículo',
      text: `Deseja remover o veículo ${vehicle.model}?`,
      showCancelButton: true,
      confirmButtonText: 'Sim, remover',
      cancelButtonText: 'Cancelar'
    }).then((result) => {
      if (result.isConfirmed) {
        this.vehicleService.delete(vehicle.id).subscribe({
          next: () => {
            Swal.fire({
              icon: 'success',
              title: 'Veículo removido',
              text: `${vehicle.model} foi removido com sucesso.`
            });
            this.loadVehicles();
          },
          error: (error) => {
            Swal.fire({
              icon: 'error',
              title: 'Erro ao remover veículo',
              text: error?.error ?? 'Não foi possível remover o veículo.'
            });
          }
        });
      }
    });
  }

  trackByVehicle(_: number, vehicle: Vehicle): number {
    return vehicle.id;
  }
}

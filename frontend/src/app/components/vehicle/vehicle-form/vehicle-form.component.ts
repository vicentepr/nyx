import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AsyncPipe, NgIf } from '@angular/common';
import Swal from 'sweetalert2';

import { VehicleService } from '@services/vehicle/vehicle.service';
import { Vehicle, VehiclePayload } from '@models/vehicle';
import { Manufacturer } from '@models/manufacturer';
import { ManufacturerSelectorModalComponent } from '../../manufacturer/modals/manufacturer-selector-modal.component';

@Component({
  selector: 'app-vehicle-form',
  standalone: true,
  imports: [ReactiveFormsModule, NgIf, AsyncPipe, ManufacturerSelectorModalComponent],
  templateUrl: './vehicle-form.component.html',
  styleUrls: ['./vehicle-form.component.scss']
})
export class VehicleFormComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly vehicleService = inject(VehicleService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  readonly vehicleId = signal<number | null>(null);
  readonly manufacturer = signal<Manufacturer | null>(null);
  readonly showManufacturerModal = signal(false);

  readonly form = this.fb.nonNullable.group({
    model: ['', Validators.required],
    licensePlate: ['', Validators.required],
    year: [new Date().getFullYear(), [Validators.required, Validators.min(1950)]],
    price: [0, [Validators.required, Validators.min(0)]],
    mileage: [0, [Validators.required, Validators.min(0)]],
    color: ['', Validators.required],
    fuelType: ['FLEX', Validators.required]
  });

  readonly pageTitle = computed(() => (this.vehicleId() ? 'Editar veículo' : 'Novo veículo'));

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (id) {
      this.vehicleId.set(id);
      this.loadVehicle(id);
    }
  }

  private loadVehicle(id: number): void {
    this.vehicleService.findById(id).subscribe({
      next: (vehicle) => this.fillForm(vehicle),
      error: (error) => {
        Swal.fire({
          icon: 'error',
          title: 'Erro ao carregar veículo',
          text: error?.error ?? 'Não foi possível carregar os dados do veículo.'
        });
        this.router.navigate(['/veiculos']);
      }
    });
  }

  private fillForm(vehicle: Vehicle): void {
    this.form.patchValue({
      model: vehicle.model,
      licensePlate: vehicle.licensePlate,
      year: vehicle.year,
      price: vehicle.price,
      mileage: vehicle.mileage,
      color: vehicle.color,
      fuelType: vehicle.fuelType
    });
    this.manufacturer.set(vehicle.manufacturer);
  }

  onSubmit(): void {
    if (this.form.invalid || !this.manufacturer()) {
      this.form.markAllAsTouched();
      if (!this.manufacturer()) {
        Swal.fire({
          icon: 'warning',
          title: 'Selecione um fabricante',
          text: 'Escolha um fabricante antes de salvar.'
        });
      }
      return;
    }

    const payload: VehiclePayload = {
      ...this.form.getRawValue(),
      manufacturerId: this.manufacturer()!.id
    };

    const request$ = this.vehicleId()
      ? this.vehicleService.update(this.vehicleId()!, payload)
      : this.vehicleService.create(payload);

    request$.subscribe({
      next: () => {
        Swal.fire({
          icon: 'success',
          title: 'Sucesso',
          text: 'Veículo salvo com sucesso.'
        });
        this.router.navigate(['/veiculos']);
      },
      error: (error) => {
        Swal.fire({
          icon: 'error',
          title: 'Erro ao salvar',
          text: error?.error ?? 'Não foi possível salvar o veículo.'
        });
      }
    });
  }

  openManufacturerModal(): void {
    this.showManufacturerModal.set(true);
  }

  closeManufacturerModal(): void {
    this.showManufacturerModal.set(false);
  }

  onManufacturerSelected(manufacturer: Manufacturer): void {
    this.manufacturer.set(manufacturer);
    this.closeManufacturerModal();
  }
}

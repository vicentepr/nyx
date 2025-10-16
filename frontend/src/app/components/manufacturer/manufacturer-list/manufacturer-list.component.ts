import { Component, OnInit, inject, signal } from '@angular/core';
import { NgFor, NgIf } from '@angular/common';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import Swal from 'sweetalert2';
import { RouterLink } from '@angular/router';

import { Manufacturer, ManufacturerFilter } from '@models/manufacturer';
import { ManufacturerService } from '@services/manufacturer/manufacturer.service';

@Component({
  selector: 'app-manufacturer-list',
  standalone: true,
  imports: [NgFor, NgIf, ReactiveFormsModule, RouterLink],
  templateUrl: './manufacturer-list.component.html',
  styleUrls: ['./manufacturer-list.component.scss']
})
export class ManufacturerListComponent implements OnInit {
  private readonly manufacturerService = inject(ManufacturerService);
  private readonly fb = inject(FormBuilder);

  protected readonly filterForm = this.fb.nonNullable.group({
    name: [''],
    country: ['']
  });

  protected readonly manufacturers = signal<Manufacturer[]>([]);
  protected readonly loading = signal(false);

  ngOnInit(): void {
    this.loadManufacturers();
  }

  loadManufacturers(): void {
    this.loading.set(true);
    const filter = this.filterForm.getRawValue() as ManufacturerFilter;
    this.manufacturerService.list(filter).subscribe({
      next: (manufacturers) => {
        this.manufacturers.set(manufacturers);
        this.loading.set(false);
      },
      error: (error) => {
        this.loading.set(false);
        Swal.fire({
          icon: 'error',
          title: 'Erro ao carregar fabricantes',
          text: error?.error ?? 'Ocorreu um erro inesperado.'
        });
      }
    });
  }

  onSearch(): void {
    this.loadManufacturers();
  }

  onReset(): void {
    this.filterForm.reset({ name: '', country: '' });
    this.loadManufacturers();
  }

  onDelete(manufacturer: Manufacturer): void {
    Swal.fire({
      icon: 'warning',
      title: 'Remover fabricante',
      text: `Deseja remover ${manufacturer.name}?`,
      showCancelButton: true,
      confirmButtonText: 'Sim, remover',
      cancelButtonText: 'Cancelar'
    }).then((result) => {
      if (result.isConfirmed) {
        this.manufacturerService.delete(manufacturer.id).subscribe({
          next: () => {
            Swal.fire({
              icon: 'success',
              title: 'Fabricante removido',
              text: `${manufacturer.name} foi removido com sucesso.`
            });
            this.loadManufacturers();
          },
          error: (error) => {
            Swal.fire({
              icon: 'error',
              title: 'Erro ao remover fabricante',
              text: error?.error ?? 'Não foi possível remover o fabricante.'
            });
          }
        });
      }
    });
  }
}

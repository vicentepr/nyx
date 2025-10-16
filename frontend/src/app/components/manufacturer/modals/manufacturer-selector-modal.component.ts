import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges, inject, signal } from '@angular/core';
import { NgClass, NgFor, NgIf } from '@angular/common';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import Swal from 'sweetalert2';

import { Manufacturer } from '@models/manufacturer';
import { ManufacturerService } from '@services/manufacturer/manufacturer.service';

@Component({
  selector: 'app-manufacturer-selector-modal',
  standalone: true,
  imports: [NgIf, NgFor, NgClass, ReactiveFormsModule],
  templateUrl: './manufacturer-selector-modal.component.html',
  styleUrls: ['./manufacturer-selector-modal.component.scss']
})
export class ManufacturerSelectorModalComponent implements OnChanges {
  private readonly manufacturerService = inject(ManufacturerService);
  private readonly fb = inject(FormBuilder);

  @Input() opened = false;
  @Output() closed = new EventEmitter<void>();
  @Output() selected = new EventEmitter<Manufacturer>();

  protected readonly searchControl = this.fb.nonNullable.control('');
  protected readonly manufacturers = signal<Manufacturer[]>([]);

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['opened']?.currentValue) {
      this.loadManufacturers();
    }
  }

  private loadManufacturers(): void {
    this.manufacturerService.list({ name: this.searchControl.value }).subscribe({
      next: (manufacturers) => this.manufacturers.set(manufacturers),
      error: (error) => {
        Swal.fire({
          icon: 'error',
          title: 'Erro ao carregar fabricantes',
          text: error?.error ?? 'Não foi possível carregar os fabricantes.'
        });
      }
    });
  }

  onSearch(): void {
    this.loadManufacturers();
  }

  onSelect(manufacturer: Manufacturer): void {
    this.selected.emit(manufacturer);
  }

  onClose(): void {
    this.closed.emit();
  }
}

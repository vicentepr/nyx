import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import Swal from 'sweetalert2';
import { NgIf } from '@angular/common';

import { ManufacturerService } from '@services/manufacturer/manufacturer.service';
import { Manufacturer } from '@models/manufacturer';

@Component({
  selector: 'app-manufacturer-form',
  standalone: true,
  imports: [ReactiveFormsModule, NgIf],
  templateUrl: './manufacturer-form.component.html',
  styleUrls: ['./manufacturer-form.component.scss']
})
export class ManufacturerFormComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly manufacturerService = inject(ManufacturerService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  readonly manufacturerId = signal<number | null>(null);

  readonly form = this.fb.nonNullable.group({
    name: ['', Validators.required],
    country: ['', Validators.required],
    foundedAt: ['', Validators.required]
  });

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (id) {
      this.manufacturerId.set(id);
      this.loadManufacturer(id);
    }
  }

  private loadManufacturer(id: number): void {
    this.manufacturerService.findById(id).subscribe({
      next: (manufacturer) => this.fillForm(manufacturer),
      error: (error) => {
        Swal.fire({
          icon: 'error',
          title: 'Erro ao carregar fabricante',
          text: error?.error ?? 'Não foi possível carregar os dados do fabricante.'
        });
        this.router.navigate(['/fabricantes']);
      }
    });
  }

  private fillForm(manufacturer: Manufacturer): void {
    this.form.patchValue({
      name: manufacturer.name,
      country: manufacturer.country,
      foundedAt: manufacturer.foundedAt
    });
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const payload = this.form.getRawValue();
    const request$ = this.manufacturerId()
      ? this.manufacturerService.update(this.manufacturerId()!, payload)
      : this.manufacturerService.create(payload);

    request$.subscribe({
      next: () => {
        Swal.fire({
          icon: 'success',
          title: 'Sucesso',
          text: 'Fabricante salvo com sucesso.'
        });
        this.router.navigate(['/fabricantes']);
      },
      error: (error) => {
        Swal.fire({
          icon: 'error',
          title: 'Erro ao salvar',
          text: error?.error ?? 'Não foi possível salvar o fabricante.'
        });
      }
    });
  }
}

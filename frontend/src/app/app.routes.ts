import { Routes } from '@angular/router';

import { LoginComponent } from './components/login/login.component';
import { MainLayoutComponent } from './layout/main-layout/main-layout.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { VehicleListComponent } from './components/vehicle/vehicle-list/vehicle-list.component';
import { VehicleFormComponent } from './components/vehicle/vehicle-form/vehicle-form.component';
import { ManufacturerListComponent } from './components/manufacturer/manufacturer-list/manufacturer-list.component';
import { ManufacturerFormComponent } from './components/manufacturer/manufacturer-form/manufacturer-form.component';
import { AuthGuard } from './guards/auth.guard';

export const appRoutes: Routes = [
  {
    path: 'login',
    component: LoginComponent
  },
  {
    path: '',
    component: MainLayoutComponent,
    canActivateChild: [AuthGuard],
    children: [
      { path: '', component: DashboardComponent },
      { path: 'veiculos', component: VehicleListComponent },
      { path: 'veiculos/novo', component: VehicleFormComponent },
      { path: 'veiculos/:id', component: VehicleFormComponent },
      { path: 'fabricantes', component: ManufacturerListComponent },
      { path: 'fabricantes/novo', component: ManufacturerFormComponent },
      { path: 'fabricantes/:id', component: ManufacturerFormComponent }
    ]
  },
  {
    path: '**',
    redirectTo: ''
  }
];

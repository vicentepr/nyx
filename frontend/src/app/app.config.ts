import { ApplicationConfig, importProvidersFrom } from '@angular/core';
import { provideAnimations } from '@angular/platform-browser/animations';
import { provideForms } from '@angular/forms';
import { MDBBootstrapModule } from 'mdb-angular-ui-kit';

export const appConfig: ApplicationConfig = {
  providers: [
    importProvidersFrom(MDBBootstrapModule.forRoot()),
    provideForms(),
    provideAnimations()
  ]
};

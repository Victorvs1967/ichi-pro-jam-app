import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { LayoutComponent } from 'src/app/components/navigation/layout/layout.component';
import { HeaderComponent } from 'src/app/components/navigation/header/header.component';
import { FooterComponent } from 'src/app/components/navigation/footer/footer.component';
import { MaterialUiModule } from '../material-ui/material-ui.module';


@NgModule({
  declarations: [
    LayoutComponent,
    HeaderComponent,
    FooterComponent
  ],
  imports: [
    CommonModule,
    RouterModule,
    MaterialUiModule
  ],
  exports: [
    LayoutComponent,
  ]
})
export class NavigationModule { }

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';

const MATERIAL_COMPONENTS = [
  MatGridListModule,
  MatCardModule,
  MatButtonModule,
  MatInputModule,
  MatListModule,
  MatToolbarModule,
  MatIconModule,
  MatDividerModule,
]

@NgModule({
  declarations: [],
  imports: [ CommonModule, ...MATERIAL_COMPONENTS ],
  exports: [ ...MATERIAL_COMPONENTS ]
})

export class MaterialUiModule { }

import { Component } from '@angular/core';
import { NgFor, NgIf } from '@angular/common';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [NgFor, NgIf],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent {
  cards = [
    {
      title: 'Veículos cadastrados',
      metric: 'veiculos',
      value: 0
    },
    {
      title: 'Fabricantes ativos',
      metric: 'fabricantes',
      value: 0
    },
    {
      title: 'Ticket médio',
      metric: 'ticket-medio',
      value: 0
    }
  ];
}

import { Component, OnInit } from '@angular/core';
import { TeamService } from 'src/app/services/team.service';

@Component({
  selector: 'app-simulation-toolbar',
  templateUrl: './simulation-toolbar.component.html',
  styleUrls: ['./simulation-toolbar.component.scss']
})
export class SimulationToolbarComponent implements OnInit {

  simulationCount = 15;

  constructor(private teameService: TeamService) { }

  ngOnInit(): void {
  }

  public simulateClicked(event: any): void {
    this.teameService.randomSimulation(this.simulationCount);
  }

  public resetClicked(event: any): void {
    this.teameService.resetScore();
  }
}

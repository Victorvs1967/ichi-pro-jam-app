import { Component, OnInit } from '@angular/core';
import { TeamModel } from 'src/app/models/team.model';
import { TeamService } from 'src/app/services/team.service';

@Component({
  selector: 'app-matchup',
  templateUrl: './matchup.component.html',
  styleUrls: ['./matchup.component.scss']
})
export class MatchupComponent implements OnInit {

  teams: TeamModel[] | any = [];

  constructor(private teamService: TeamService) { }

  ngOnInit(): void {
    this.loadTeams();
  }

  private loadTeams(): void {

    this.teamService._teamsSource.subscribe((value: any) => {
      if (value !== undefined && value !== null) {
        this.teams = [].concat(value);
      }
    });

    this.teamService._teamWatchSource.subscribe(updatedTeam => {
      if (updatedTeam !== undefined && updatedTeam.name !== undefined) {
        let name = updatedTeam.name;
        let length = this.teams.length;

        for (let i = 0; i < length; i++) {
          if (this.teams[i].name === name) {
            this.teams[i] = updatedTeam;
          }
        }
        this.teams = [].concat(this.teams);
      }
    });
  }

}

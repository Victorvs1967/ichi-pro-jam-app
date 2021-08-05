import { Injectable, NgZone } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { TeamModel } from '../models/team.model';

@Injectable({
  providedIn: 'root'
})
export class TeamService {

  private teamsWatchUrl = environment.backendUrl + environment.watchTeamsPath;
  private teamWatchUrl = environment.backendUrl + environment.watchTeamPath;
  private allTeamsUrl = environment.backendUrl + environment.getAllTeamsPath;
  private updatePlayerUrl = environment.backendUrl + environment.updatePlayerPath;
  private zeroScoreUrl = environment.backendUrl + environment.zeroScorePath;
  private randomSimulationPath = environment.backendUrl + environment.updateRandomPath;

  private teamsSource = new BehaviorSubject([]);
  _teamsSource: Observable<TeamModel[]> = this.teamsSource.asObservable();
  
  private teamWatchSource = new BehaviorSubject(new TeamModel());
  _teamWatchSource: Observable<TeamModel> = this.teamWatchSource.asObservable();
  
  constructor(private http: HttpClient, private zone: NgZone) {

    this.getTeams().subscribe(data => {
      let teams: any = data as TeamModel[];
      this.teamsSource.next(teams);
    }, error => console.log('Error: ' + error),
    () => console.log('done loading teams'));

    this.getTeamsStream().subscribe(data => {
          this.teamWatchSource.next(new TeamModel().deserialize(data))},
                                    error => console.log('Error: ' + error),
                                    () => console.log('done loading team stream'));
  }

  getTeams(): Observable<TeamModel[]> {
    return this.http.get<TeamModel[]>(this.allTeamsUrl);
  }

  updatePlayer(playerName: string, scoreChange: number): void {
    let url = this.updatePlayerUrl.replace('{name}', playerName);
    url = url.replace('{scoreChange}', '' + scoreChange);

    this.http.get(url).subscribe();
  }

  randomSimulation(count: number): void {
    let url = this.randomSimulationPath.replace('{count}', '' + count);
    this.http.get(url).subscribe();
  }

  resetScore(): void {
    this.http.get(this.zeroScoreUrl).subscribe();
  }

  getTeamsStream(): Observable<TeamModel> {
    return new Observable((observer: any) => {
      let url = this.teamsWatchUrl;
      let eventSource = new EventSource(url);

      eventSource.onmessage = event => {
        let json = JSON.parse(event.data);
        if (json !== undefined && json !== '') {
          this.zone.run(() => observer.next(json));
        }
      };

      eventSource.onerror = error => {
        if (eventSource.readyState === 0) {
          console.log('The stream has been closed by the server');
          eventSource.close();
          observer.complete();
        } else {
          observer.error('EventSource error: ' + error);
        }
      }
    });
  }
}

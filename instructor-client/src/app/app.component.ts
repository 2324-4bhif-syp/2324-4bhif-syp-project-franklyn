import {Component, Injectable} from '@angular/core';
import { CommonModule, Location } from '@angular/common';
import {RouterLinkActive, RouterOutlet, RouterModule} from '@angular/router';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {PatrolModeComponent} from "./component/router-components/patrol-mode/patrol-mode.component";
import {VideoViewerComponent} from "./component/router-components/video-viewer/video-viewer.component";
import ExamineeDataService from "./shared/repository/examinee-data.service";
import {environment} from "../../env/environment";
import {PatrolManagerService} from "./shared/repository/patrol-manager.service";

@Injectable({
  providedIn: 'root'
})
@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule,
    RouterOutlet,
    FormsModule,
    RouterModule,
    RouterLinkActive,
    PatrolModeComponent,
    VideoViewerComponent,
    ReactiveFormsModule
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent{
  constructor(private examineeRepo: ExamineeDataService, protected location: Location, private patrolManagerService: PatrolManagerService) {
    this.changeRoute(); // call it for initialization in case of for example a page reload

    setInterval(() => {
      examineeRepo.getAllExamineesFromServer();
    }, environment.nextClientScheduleTime*1000);
  }

  public patrolModeActive: boolean = false;

  public changeRoute() {
    if (this.location.path() === "/video-viewer") {
      this.patrolModeActive = false;
      this.patrolManagerService.stopInterval();
    } else if (this.location.path() === "/metrics-dashboard") {
      this.patrolModeActive = false;
      this.patrolManagerService.stopInterval();
    } else {
      this.patrolModeActive = true;
      this.patrolManagerService.startInterval();
    }

    this.examineeRepo.patrolModeOn = false; //safety measure to prevent any possible bugs
    this.examineeRepo.unsetPatrolExaminee();
  }
}

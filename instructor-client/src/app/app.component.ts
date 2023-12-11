import {Component, Injectable} from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import {ExamineeComponent} from "./component/examinee/examinee.component";
import ExamineeDataService from "./shared/repository/examinee-data.service";
import {ExamineeListComponent} from "./component/examinee-list/examinee-list.component";
import {environment} from "../environment/environment";
import {Examinee} from "./shared/entity/Examinee";

@Injectable({
  providedIn: 'root'
})
@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, ExamineeComponent, ExamineeListComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  constructor(protected examineeRepo: ExamineeDataService) {
    setInterval(() => {
      examineeRepo.getAllExamineesFromServer();
    }, environment.nextClientScheduleTime);
  }
}

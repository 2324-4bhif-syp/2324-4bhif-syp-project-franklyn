import {Component, Injectable} from '@angular/core';
import {CommonModule} from "@angular/common";
import {ExamineeComponent} from "../examinee/examinee.component";
import ExamineeDataService from "../../shared/repository/examinee-data.service";

@Injectable({
  providedIn: 'root'
})
@Component({
  selector: 'app-examinee-list',
  standalone: true,
  imports: [
    CommonModule,
    ExamineeComponent
  ],
  templateUrl: './examinee-list.component.html',
  styleUrl: './examinee-list.component.css'
})
export class ExamineeListComponent {
  constructor(protected examineeRepo: ExamineeDataService) {
  }
}

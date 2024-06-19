import { Component } from '@angular/core';
import {BaseChartDirective} from "ng2-charts";
import {ExamineeListComponent} from "../../examinee-lists/examinee-list/examinee-list.component";
import {PatrolPageExamineeComponent} from "../../examinee-versions/patrol-page-examinee/patrol-page-examinee.component";

@Component({
  selector: 'app-test-overview',
  standalone: true,
  imports: [
    BaseChartDirective,
    ExamineeListComponent,
    PatrolPageExamineeComponent
  ],
  templateUrl: './test-overview.component.html',
  styleUrl: './test-overview.component.css'
})
export class TestOverviewComponent {

}

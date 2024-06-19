import { Component } from '@angular/core';
import {BaseChartDirective} from "ng2-charts";

@Component({
  selector: 'app-test-overview',
  standalone: true,
  imports: [
    BaseChartDirective
  ],
  templateUrl: './test-overview.component.html',
  styleUrl: './test-overview.component.css'
})
export class TestOverviewComponent {

}

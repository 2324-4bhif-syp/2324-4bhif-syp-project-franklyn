import {Component, inject, OnDestroy, OnInit, QueryList, ViewChildren} from '@angular/core';
import {BaseChartDirective} from "ng2-charts";
import {StoreService} from "../../../services/store.service";
import {ChartConfiguration, ChartData, ChartType} from "chart.js";
import {WebApiService} from "../../../services/web-api.service";
import {distinctUntilChanged, map} from "rxjs";
import {environment} from "../../../../../env/environment";
import {set} from "../../../model";

@Component({
  selector: 'app-metrics-dashboard',
  standalone: true,
  imports: [
    BaseChartDirective
  ],
  templateUrl: './metrics-dashboard.component.html',
  styleUrl: './metrics-dashboard.component.css'
})
export class MetricsDashboardComponent implements OnInit, OnDestroy{
  @ViewChildren(BaseChartDirective) charts: QueryList<BaseChartDirective> | undefined;

  protected store = inject(StoreService).store;
  protected webApi = inject(WebApiService);

  ngOnInit() {
    // subscribe to server-metrics to update when
    // there are changes
    this.store.pipe(
      map(store => store.serverMetrics),
      distinctUntilChanged()
    ).subscribe(next => {
      this.updateDatasets();
    });

    // get the server-metrics periodically
    set((model) => {
      model.serverMetrics.timerId = setInterval(() => {
        this.webApi.getServerMetrics();
      }, environment.reloadDashboardInterval)
    })
  }

  ngOnDestroy() {
    clearTimeout(this.store.value.serverMetrics.timerId);
    set((model) => {
      model.serverMetrics.timerId = undefined;
    })
  }

  updateDatasets() {
    this.diskChartData.datasets[0].data = [
      this.store.value.serverMetrics.remainingDiskSpaceInBytes / (1024 * 1024 * 1024),
      this.store.value.serverMetrics.savedScreenshotsSizeInBytes / (1024 * 1024 * 1024),
    ];
    this.diskChartData.labels![this.memChartData.labels!.length - 1] = `Disk usage: ${this.diskChartData.datasets[0].data[1].toFixed(2)} GiB`;

    this.memChartData.datasets[0].data = [
      (this.store.value.serverMetrics.maxAvailableMemoryInBytes - this.store.value.serverMetrics.totalUsedMemoryInBytes)/ (1024 * 1024 * 1024),
      this.store.value.serverMetrics.totalUsedMemoryInBytes / (1024 * 1024 * 1024),
    ];
    this.memChartData.labels![this.memChartData.labels!.length - 1] = `Memory utilization: ${(this.store.value.serverMetrics.totalUsedMemoryInBytes / this.store.value.serverMetrics.maxAvailableMemoryInBytes * 100).toFixed(2)}%`;

    this.cpuChartData.datasets[0].data = [
      this.store.value.serverMetrics.cpuUsagePercent,
      100 - this.store.value.serverMetrics.cpuUsagePercent
    ]

    this.charts?.forEach(c => c.update());
  }

  doughnutLabel = {
    id: 'doughnutLabel',
    beforeDatasetsDraw(chart:  any, args:  any, options: any): boolean | void {
      const { ctx, data } = chart;

      ctx.save();
      const x = chart.getDatasetMeta(0).data[0].x;
      const y = chart.getDatasetMeta(0).data[0].y;
      ctx.font = "bold 15px sans-serif";
      ctx.fillStyle = "#36363a";
      ctx.textAlign = "center";
      ctx.textBaseline = "middle";
      ctx.fillText(data.labels[data.labels.length - 1], x, y);
    }
  };

  protected diskChartType: ChartType = "doughnut";
  protected diskChartData: ChartData<'doughnut', number[], string> = {
    labels: [ "Free", "Screenshots", "Disk usage"],
    datasets: [
      {
        data: [
          this.store.value.serverMetrics.remainingDiskSpaceInBytes / (1024 * 1024 * 1024),
          this.store.value.serverMetrics.savedScreenshotsSizeInBytes / (1024 * 1024 * 1024),
        ],
        backgroundColor: [
          "#f0f0f0",
          "#0d6efd"
        ]
      }
    ]
  };

  protected diskChartOptions: ChartConfiguration['options'] = {
    responsive: true,
    plugins: {
      legend: {
        display: false,
      },
      tooltip: {
        callbacks: {
          label: (ttItem) => (`${ttItem.parsed.toFixed(2)} GiB`)
        }
      }
    }
  };

  protected memChartType: ChartType = "doughnut"
  protected memChartData: ChartData<'doughnut', number[], string> = {
    labels: [ "Free", "Used", "Memory utilization"],
    datasets: [
      {
        data: [
          (this.store.value.serverMetrics.maxAvailableMemoryInBytes - this.store.value.serverMetrics.totalUsedMemoryInBytes)/ (1024 * 1024 * 1024),
          this.store.value.serverMetrics.totalUsedMemoryInBytes / (1024 * 1024 * 1024),
        ],
        backgroundColor: [
          "#f0f0f0",
          "#0d6efd"
        ]
      }
    ]
  };

  protected memChartOptions: ChartConfiguration['options'] = {
    responsive: true,
    plugins: {
      legend: {
        display: false,
      },
      tooltip: {
        callbacks: {
          label: (ttItem) => (`${ttItem.parsed.toFixed(2)} GiB`)
        }
      }
    }
  };

  protected cpuChartType = "bar" as const;
  protected cpuChartData: ChartData<'bar', number[], string> = {
    labels: [ "CPU Utilization"],
    datasets: [
      {
        data: [
          100 - this.store.value.serverMetrics.cpuUsagePercent
        ],
        backgroundColor: [
          "#0d6efd",
        ]
      },
      {
        data: [
          100 - this.store.value.serverMetrics.cpuUsagePercent
        ],
        backgroundColor: [
          "#f0f0f0"
        ]
      }
    ]
  };

  protected cpuChartOptions: ChartConfiguration<'bar'>['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    indexAxis: "y",
    scales: {
      x: {
        display: false,
        stacked: true,
        min: 0,
        max: 100
      },
      y: {
        display: false,
        stacked: true,
        min: 0,
        max: 100
      }
    },
    plugins: {
      legend: {
        display: false,
      },
      tooltip: {
        callbacks: {
          label: (ttItem) => {
            if(ttItem.datasetIndex == 0) {
              return(`${ttItem.parsed.x.toFixed(2)} %`)
            } else {
              return "";
            }
          }
        }
      }
    }
  };
}

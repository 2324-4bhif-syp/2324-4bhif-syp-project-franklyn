import {Component, QueryList, ViewChild, ViewChildren} from '@angular/core';
import {WebApiService} from "../../../shared/web-api.service";
import {ServerMetrics} from "../../../shared/entity/ServerMetrics";
import {Chart, ChartConfiguration, ChartData, ChartEvent, ChartType} from 'chart.js';
import { BaseChartDirective } from 'ng2-charts';

@Component({
  selector: 'app-metrics-dashboard',
  standalone: true,
  imports: [
    BaseChartDirective
  ],
  templateUrl: './metrics-dashboard.component.html',
  styleUrl: './metrics-dashboard.component.css'
})
export class MetricsDashboardComponent {
  @ViewChildren(BaseChartDirective) charts: QueryList<BaseChartDirective> | undefined;

  protected serverMetrics: ServerMetrics = {
    cpuUsagePercent: 0,
    maxAvailableMemoryInBytes: 0,
    remainingDiskSpaceInBytes: 0,
    savedScreenshotsSizeInBytes: 0,
    totalDiskSpaceInBytes: 0,
    totalUsedMemoryInBytes: 0
  };

  private timerId: number | undefined = undefined;

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

  protected diskChartType: ChartType = "doughnut"
  protected diskChartData: ChartData<'doughnut', number[], string> = {
    labels: [ "Free", "Screenshots", "Disk usage"],
    datasets: [
      {
        data: [
          this.serverMetrics.remainingDiskSpaceInBytes / (1024 * 1024 * 1024),
          this.serverMetrics.savedScreenshotsSizeInBytes / (1024 * 1024 * 1024),
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
  }

  protected memChartType: ChartType = "doughnut"
  protected memChartData: ChartData<'doughnut', number[], string> = {
    labels: [ "Free", "Used", "Memory utilization"],
    datasets: [
      {
        data: [
          (this.serverMetrics.maxAvailableMemoryInBytes - this.serverMetrics.totalUsedMemoryInBytes)/ (1024 * 1024 * 1024),
          this.serverMetrics.totalUsedMemoryInBytes / (1024 * 1024 * 1024),
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
  }

  protected cpuChartType = "bar" as const;
  protected cpuChartData: ChartData<'bar', number[], string> = {
    labels: [ "CPU Utilization"],
    datasets: [
      {
        data: [
          100 - this.serverMetrics.cpuUsagePercent
        ],
        backgroundColor: [
          "#0d6efd",
        ]
      },
      {
        data: [
          100 - this.serverMetrics.cpuUsagePercent
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
  }

  constructor(protected webApi: WebApiService) {
    webApi.getServerMetrics().subscribe(next => {
      this.serverMetrics = next;
      this.updateDatasets();
    });
  }

  ngOnInit() {
    this.timerId = setInterval(() => {
      this.webApi.getServerMetrics().subscribe(next => {
        this.serverMetrics = next;
        this.updateDatasets();
      });
    }, 5000);
  }

  ngOnDestroy() {
    clearTimeout(this.timerId);
  }

  updateDatasets() {
    this.diskChartData.datasets[0].data = [
      this.serverMetrics.remainingDiskSpaceInBytes / (1024 * 1024 * 1024),
      this.serverMetrics.savedScreenshotsSizeInBytes / (1024 * 1024 * 1024),
    ];
    this.diskChartData.labels![this.memChartData.labels!.length - 1] = `Disk usage: ${this.diskChartData.datasets[0].data[1].toFixed(2)} GiB`;

    this.memChartData.datasets[0].data = [
      (this.serverMetrics.maxAvailableMemoryInBytes - this.serverMetrics.totalUsedMemoryInBytes)/ (1024 * 1024 * 1024),
      this.serverMetrics.totalUsedMemoryInBytes / (1024 * 1024 * 1024),
    ];
    this.memChartData.labels![this.memChartData.labels!.length - 1] = `Memory utilization: ${(this.serverMetrics.totalUsedMemoryInBytes / this.serverMetrics.maxAvailableMemoryInBytes * 100).toFixed(2)}%`;

    this.cpuChartData.datasets[0].data = [
      this.serverMetrics.cpuUsagePercent,
      100 - this.serverMetrics.cpuUsagePercent
    ]

    this.charts?.forEach(c => c.update());
  }
}

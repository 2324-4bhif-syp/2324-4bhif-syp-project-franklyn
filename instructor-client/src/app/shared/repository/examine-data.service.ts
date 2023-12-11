import { Injectable } from "@angular/core";
import { Examine } from "../entity/Examine";
import {WebApiService} from "../web-api.service";

@Injectable({
  providedIn: 'root'
})
export default class ExamineDataService {
  constructor(private webApi: WebApiService) {
    this.getAllExaminesFromServer();
  }

  protected items: Examine[] = [];
  protected patrolExamine: Examine | undefined;
  protected reloadNumber: number = 0;

  //updates the list of examines via the server
  getAllExaminesFromServer(): void {
    this.webApi.getClientsFromServer().subscribe({
      next: (examines) => {
        this.items = examines;
      },
      error: (err) => console.error(err),
    });
    this.newPatrolExamine();
  }

  get(predicate?: ((item: Examine) => boolean) | undefined): Examine[] {
    if (predicate) return this.get().filter(predicate);
    return this.items;
  }

  getPatrolExamine(): Examine | undefined {
    if (this.items.length === 0) {
      this.patrolExamine = undefined;
    }

    return this.patrolExamine;
  }

  private newPatrolExamine() {
    let examinees: Examine[] = this.get();

    if (examinees.length === 0 && this.items.length === 0) {
      this.patrolExamine = undefined;
    } else {
      if (examinees.length === 0) {
        this.patrolExamine = this.items[0];
      } else {
        this.patrolExamine = examinees[Math.floor(Math.random() * examinees.length)];
      }
      this.reloadNumber++;
    }
  }

  getReloadNumber(): number {
    return this.reloadNumber
  }
}

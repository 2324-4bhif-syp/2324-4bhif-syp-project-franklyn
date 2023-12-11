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

  //updates the list of examines via the server
  getAllExaminesFromServer(): void {
    this.webApi.getClientsFromServer().subscribe({
      next: (examines) => {
        this.items = examines;
      },
      error: (err) => console.error(err),
    });
  }

  get(predicate?: ((item: Examine) => boolean) | undefined): Examine[] {
    if (predicate) return this.get().filter(predicate);
    return this.items;
  }

  getPatrolExamine(): Examine | undefined {
    if (this.items.length === 0) {
      this.patrolExamine = undefined;
    } else {
      // TODO: use worker to cycle
      this.patrolExamine = this.items[0];
    }

    return this.patrolExamine;
  }
}

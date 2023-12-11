import { TestBed } from '@angular/core/testing';

import { ExamineDataService } from './examine-data.service';

describe('ExamineDataService', () => {
  let service: ExamineDataService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ExamineDataService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

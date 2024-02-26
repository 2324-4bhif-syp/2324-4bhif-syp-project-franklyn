import { TestBed } from '@angular/core/testing';

import { PatrolManagerService } from './patrol-manager.service';

describe('PatrolManagerService', () => {
  let service: PatrolManagerService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PatrolManagerService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

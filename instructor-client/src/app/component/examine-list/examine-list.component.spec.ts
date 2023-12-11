import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExamineListComponent } from './examine-list.component';

describe('ExamineListComponent', () => {
  let component: ExamineListComponent;
  let fixture: ComponentFixture<ExamineListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ExamineListComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ExamineListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

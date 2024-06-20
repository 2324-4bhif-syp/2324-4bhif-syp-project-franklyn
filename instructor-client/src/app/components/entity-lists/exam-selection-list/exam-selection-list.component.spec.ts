import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExamSelectionListComponent } from './exam-selection-list.component';

describe('ExamSelectionListComponent', () => {
  let component: ExamSelectionListComponent;
  let fixture: ComponentFixture<ExamSelectionListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ExamSelectionListComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ExamSelectionListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

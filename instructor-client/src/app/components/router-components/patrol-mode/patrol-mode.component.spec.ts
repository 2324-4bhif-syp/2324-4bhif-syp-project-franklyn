import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PatrolModeComponent } from './patrol-mode.component';

describe('PatrolModeComponent', () => {
  let component: PatrolModeComponent;
  let fixture: ComponentFixture<PatrolModeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PatrolModeComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(PatrolModeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

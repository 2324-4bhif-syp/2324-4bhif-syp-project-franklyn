import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditTestViewComponent } from './edit-test-view.component';

describe('EditTestViewComponent', () => {
  let component: EditTestViewComponent;
  let fixture: ComponentFixture<EditTestViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditTestViewComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(EditTestViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

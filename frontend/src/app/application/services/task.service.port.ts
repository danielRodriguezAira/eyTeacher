import {Observable} from 'rxjs';
import {Task} from '../../domain/entities/task';

export interface TaskServicePort {
    getTaskById(id: number): Observable<Task>;
    saveTask(task: Task): Observable<any>;
    deleteTask(id: number): Observable<void>;
}

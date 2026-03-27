import {Observable} from 'rxjs';
import {Task} from '../../domain/entities/task';
import {StudentTasksResponse} from '../../domain/entities/student-tasks-response';

export interface TaskServicePort {
    getTaskById(id: number): Observable<Task>;
    saveTask(task: Task): Observable<any>;
    deleteTask(id: number): Observable<void>;
    getStudentTasks(studentId: string): Observable<StudentTasksResponse[]>;
}

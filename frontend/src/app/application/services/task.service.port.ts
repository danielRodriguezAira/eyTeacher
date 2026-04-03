import {Observable} from 'rxjs';
import {Task} from '../../domain/entities/task';
import {StudentTasksResponse} from '../../domain/entities/student-tasks-response';
import {PageResponse} from '../../domain/entities/page-response';

export interface TaskServicePort {
    getTaskById(id: number): Observable<Task>;
    saveTask(task: Task): Observable<any>;
    deleteTask(id: number): Observable<void>;
    getTasksByTopic(topicId: number, page: number, size: number): Observable<PageResponse<Task>>;
    getStudentTasks(studentId: string): Observable<StudentTasksResponse[]>;
}

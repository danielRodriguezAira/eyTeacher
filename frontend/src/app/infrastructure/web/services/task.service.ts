import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Task} from '../../../domain/entities/task';
import {TaskServicePort} from '../../../application/services/task.service.port';
import {StudentTasksResponse} from '../../../domain/entities/student-tasks-response';
import {PageResponse} from '../../../domain/entities/page-response';

@Injectable({
    providedIn: 'root'
})
export class TaskService implements TaskServicePort {
    private http = inject(HttpClient);
    private readonly API_URL = '/api/v1/tasks';
    private readonly EXERCISES_AI_URL = '/ai-api/api/v1/exercises';

    getTaskById(id: number): Observable<Task> {
        return this.http.get<Task>(`${this.API_URL}/${id}`);
    }

    saveTask(task: Task): Observable<any> {
        return this.http.post<any>(`${this.API_URL}`, task);
    }

    deleteTask(id: number): Observable<void> {
        return this.http.delete<void>(`${this.API_URL}/${id}`);
    }

    getTasksByTopic(topicId: number, page: number, size: number): Observable<PageResponse<Task>> {
        return this.http.get<PageResponse<Task>>(`${this.API_URL}/topic/${topicId}`, {params: {page, size}});
    }

    getStudentTasks(studentId: string): Observable<StudentTasksResponse[]> {
        return this.http.get<StudentTasksResponse[]>(`${this.API_URL}/student/${studentId}`);
    }

    generateExercise(task: string): Observable<{ taskProposal: string }> {
        return this.http.post<{ taskProposal: string }>(this.EXERCISES_AI_URL, { task });
    }
}

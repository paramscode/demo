package com.paramsandhu.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paramsandhu.demo.entity.Employee;
import com.paramsandhu.demo.entity.EmployeeStaging;
import com.paramsandhu.demo.entity.FalloutRecord;
import com.paramsandhu.demo.repository.EmployeeRepository;
import com.paramsandhu.demo.repository.EmployeeStagingRepository;
import com.paramsandhu.demo.repository.FalloutRepository;
import com.paramsandhu.demo.service.EmployeeProcessorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EmployeeControllerTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private EmployeeStagingRepository employeeStagingRepository;

    @Mock
    private FalloutRepository falloutRepository;

    @Mock
    private EmployeeProcessorService employeeProcessorService;

    @InjectMocks
    private EmployeeController employeeController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(employeeController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void createEmployee_ValidInput_ReturnsCreatedEmployee() throws Exception {
        // Prepare test data
        String employeeJson = """
            {
                "employee": {
                    "name": "John Doe",
                    "salary": 75000.00,
                    "married": true,
                    "department": "Engineering"
                }
            }
            """;

        Employee savedEmployee = new Employee();
        savedEmployee.setId(1L);
        savedEmployee.setName("John Doe");
        savedEmployee.setSalary(75000.00);
        savedEmployee.setMarried(true);
        savedEmployee.setDepartment("Engineering");

        // Mock repository responses
        when(employeeRepository.save(any(Employee.class))).thenReturn(savedEmployee);
        when(employeeProcessorService.getTaxDue(anyLong())).thenReturn(5000.0);
        when(employeeProcessorService.getAccountNumber(anyLong())).thenReturn("ACC123");

        // Perform the test
        mockMvc.perform(post("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(employeeJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.salary").value(75000.00))
                .andExpect(jsonPath("$.married").value(true))
                .andExpect(jsonPath("$.department").value("Engineering"));

        // Verify interactions
        verify(employeeRepository).save(any(Employee.class));
        verify(employeeStagingRepository).save(any(EmployeeStaging.class));
    }

    @Test
    void createEmployee_InvalidJson_ReturnsBadRequest() throws Exception {
        String invalidJson = "invalid json";

        mockMvc.perform(post("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());

        verify(falloutRepository).save(any(FalloutRecord.class));
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void getAllEmployees_ReturnsListOfEmployees() throws Exception {
        Employee employee1 = new Employee();
        employee1.setId(1L);
        employee1.setName("John Doe");
        Employee employee2 = new Employee();
        employee2.setId(2L);
        employee2.setName("Jane Doe");

        when(employeeRepository.findAll()).thenReturn(Arrays.asList(employee1, employee2));

        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("John Doe"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Jane Doe"));
    }

    @Test
    void getEmployeeById_ExistingId_ReturnsEmployee() throws Exception {
        Employee employee = new Employee();
        employee.setId(1L);
        employee.setName("John Doe");
        employee.setSalary(75000.00);
        employee.setMarried(true);
        employee.setDepartment("Engineering");

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        mockMvc.perform(get("/api/employees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.salary").value(75000.00))
                .andExpect(jsonPath("$.married").value(true))
                .andExpect(jsonPath("$.department").value("Engineering"));
    }

    @Test
    void getAllEmployeesWithPagination_ReturnsPagedResult() throws Exception {
        Employee employee1 = new Employee();
        employee1.setId(1L);
        employee1.setName("John Doe");
        Employee employee2 = new Employee();
        employee2.setId(2L);
        employee2.setName("Jane Doe");

        Page<Employee> page = new PageImpl<>(Arrays.asList(employee1, employee2));
        when(employeeRepository.findAll(any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/employees/page/0/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("John Doe"))
                .andExpect(jsonPath("$.content[1].id").value(2))
                .andExpect(jsonPath("$.content[1].name").value("Jane Doe"));
    }

    @Test
    void generateReport_Success_ReturnsOk() throws Exception {
        doNothing().when(employeeProcessorService).generateAndUploadReport();

        mockMvc.perform(get("/api/employees/report"))
                .andExpect(status().isOk())
                .andExpect(content().string("Report generated successfully"));

        verify(employeeProcessorService).generateAndUploadReport();
    }
}

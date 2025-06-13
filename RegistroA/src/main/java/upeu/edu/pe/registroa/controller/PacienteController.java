package upeu.edu.pe.registroa.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import upeu.edu.pe.registroa.model.Paciente;
import upeu.edu.pe.registroa.service.PacienteService;

import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

@Component
public class PacienteController implements Initializable {

    @FXML private TextField txtNombres;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtDni;
    @FXML private DatePicker dpFechaNacimiento;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtEmail;
    @FXML private TextField txtDireccion;
    @FXML private ComboBox<String> cbGenero;
    
    @FXML private TableView<Paciente> tablePacientes;
    @FXML private TableColumn<Paciente, Long> colId;
    @FXML private TableColumn<Paciente, String> colNombres;
    @FXML private TableColumn<Paciente, String> colApellidos;
    @FXML private TableColumn<Paciente, String> colDni;
    @FXML private TableColumn<Paciente, LocalDate> colFechaNacimiento;
    @FXML private TableColumn<Paciente, String> colTelefono;
    @FXML private TableColumn<Paciente, String> colEmail;
    
    @FXML private Button btnGuardar;
    @FXML private Button btnActualizar;
    @FXML private Button btnEliminar;
    @FXML private Button btnLimpiar;

    @Autowired
    private PacienteService pacienteService;

    private ObservableList<Paciente> pacientesList = FXCollections.observableArrayList();
    private Paciente pacienteSeleccionado;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeTable();
        initializeComboBox();
        loadPacientes();
        
        // Listener para selección en la tabla
        tablePacientes.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    pacienteSeleccionado = newSelection;
                    fillForm(newSelection);
                    btnActualizar.setDisable(false);
                    btnEliminar.setDisable(false);
                    btnGuardar.setDisable(true);
                }
            }
        );
    }

    private void initializeTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombres.setCellValueFactory(new PropertyValueFactory<>("nombres"));
        colApellidos.setCellValueFactory(new PropertyValueFactory<>("apellidos"));
        colDni.setCellValueFactory(new PropertyValueFactory<>("dni"));
        colFechaNacimiento.setCellValueFactory(new PropertyValueFactory<>("fechaNacimiento"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        
        tablePacientes.setItems(pacientesList);
    }

    private void initializeComboBox() {
        cbGenero.setItems(FXCollections.observableArrayList("Masculino", "Femenino", "Otro"));
    }

    private void loadPacientes() {
        pacientesList.clear();
        pacientesList.addAll(pacienteService.findAll());
    }

    private void fillForm(Paciente paciente) {
        txtNombres.setText(paciente.getNombres());
        txtApellidos.setText(paciente.getApellidos());
        txtDni.setText(paciente.getDni());
        dpFechaNacimiento.setValue(paciente.getFechaNacimiento());
        txtTelefono.setText(paciente.getTelefono());
        txtEmail.setText(paciente.getEmail());
        txtDireccion.setText(paciente.getDireccion());
        cbGenero.setValue(paciente.getGenero());
    }

    @FXML
    private void handleGuardar() {
        if (validateForm()) {
            try {
                Paciente paciente = new Paciente();
                paciente.setNombres(txtNombres.getText().trim());
                paciente.setApellidos(txtApellidos.getText().trim());
                paciente.setDni(txtDni.getText().trim());
                paciente.setFechaNacimiento(dpFechaNacimiento.getValue());
                paciente.setTelefono(txtTelefono.getText().trim());
                paciente.setEmail(txtEmail.getText().trim());
                paciente.setDireccion(txtDireccion.getText().trim());
                paciente.setGenero(cbGenero.getValue());

                if (pacienteService.existsByDni(paciente.getDni())) {
                    showAlert("Error", "Ya existe un paciente con este DNI", Alert.AlertType.ERROR);
                    return;
                }

                pacienteService.save(paciente);
                showAlert("Éxito", "Paciente guardado correctamente", Alert.AlertType.INFORMATION);
                loadPacientes();
                clearForm();
            } catch (Exception e) {
                showAlert("Error", "Error al guardar el paciente: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void handleActualizar() {
        if (pacienteSeleccionado != null && validateForm()) {
            try {
                pacienteSeleccionado.setNombres(txtNombres.getText().trim());
                pacienteSeleccionado.setApellidos(txtApellidos.getText().trim());
                pacienteSeleccionado.setDni(txtDni.getText().trim());
                pacienteSeleccionado.setFechaNacimiento(dpFechaNacimiento.getValue());
                pacienteSeleccionado.setTelefono(txtTelefono.getText().trim());
                pacienteSeleccionado.setEmail(txtEmail.getText().trim());
                pacienteSeleccionado.setDireccion(txtDireccion.getText().trim());
                pacienteSeleccionado.setGenero(cbGenero.getValue());

                if (pacienteService.existsByDniAndIdNot(pacienteSeleccionado.getDni(), pacienteSeleccionado.getId())) {
                    showAlert("Error", "Ya existe otro paciente con este DNI", Alert.AlertType.ERROR);
                    return;
                }

                pacienteService.save(pacienteSeleccionado);
                showAlert("Éxito", "Paciente actualizado correctamente", Alert.AlertType.INFORMATION);
                loadPacientes();
                clearForm();
            } catch (Exception e) {
                showAlert("Error", "Error al actualizar el paciente: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void handleEliminar() {
        if (pacienteSeleccionado != null) {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirmar eliminación");
            confirmAlert.setHeaderText("¿Está seguro de eliminar este paciente?");
            confirmAlert.setContentText("Esta acción no se puede deshacer.");

            Optional<ButtonType> result = confirmAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    pacienteService.deleteById(pacienteSeleccionado.getId());
                    showAlert("Éxito", "Paciente eliminado correctamente", Alert.AlertType.INFORMATION);
                    loadPacientes();
                    clearForm();
                } catch (Exception e) {
                    showAlert("Error", "Error al eliminar el paciente: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        }
    }

    @FXML
    private void handleLimpiar() {
        clearForm();
    }

    private void clearForm() {
        txtNombres.clear();
        txtApellidos.clear();
        txtDni.clear();
        dpFechaNacimiento.setValue(null);
        txtTelefono.clear();
        txtEmail.clear();
        txtDireccion.clear();
        cbGenero.setValue(null);
        
        pacienteSeleccionado = null;
        tablePacientes.getSelectionModel().clearSelection();
        
        btnGuardar.setDisable(false);
        btnActualizar.setDisable(true);
        btnEliminar.setDisable(true);
    }

    private boolean validateForm() {
        if (txtNombres.getText().trim().isEmpty()) {
            showAlert("Error de validación", "El campo Nombres es obligatorio", Alert.AlertType.WARNING);
            txtNombres.requestFocus();
            return false;
        }
        
        if (txtApellidos.getText().trim().isEmpty()) {
            showAlert("Error de validación", "El campo Apellidos es obligatorio", Alert.AlertType.WARNING);
            txtApellidos.requestFocus();
            return false;
        }
        
        if (txtDni.getText().trim().isEmpty()) {
            showAlert("Error de validación", "El campo DNI es obligatorio", Alert.AlertType.WARNING);
            txtDni.requestFocus();
            return false;
        }
        
        if (txtDni.getText().trim().length() != 8) {
            showAlert("Error de validación", "El DNI debe tener 8 dígitos", Alert.AlertType.WARNING);
            txtDni.requestFocus();
            return false;
        }
        
        return true;
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
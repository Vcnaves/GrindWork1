import { useEffect, useState } from "react";
import Api from "../api/Api";
import Swal from "sweetalert2";

import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import Paper from "@mui/material/Paper";

interface User {
  id: string;
  nome: string;
  email: string;
  status: number;
  authority: number;
  active: boolean;
  isAdmin: boolean;
}

const Usuarios = () => {
  const [users, setUsers] = useState<User[]>([]);
  const [filteredUsers, setFilteredUsers] = useState<User[]>([]);
  const [search, setSearch] = useState("");

  const mapStatus = (status: number): string => {
    return status === 1 ? "ativo" : "bloqueado";
  };

  useEffect(() => {
    const data = JSON.parse(localStorage.getItem("userManagement") || "[]");

    if (data.length > 0) {
      const formattedUsers = data.map((user: any) => ({
        id: user.id || "",
        nome: user.nome || "",
        email: user.email || "",
        status: mapStatus(user.status) || "bloqueado",
        authority: user.authorities[0]?.authority === "ROLE_ADMIN" ? 1 : 0,
        isAdmin: user.authorities[0]?.authority === "ROLE_ADMIN",
        active: user.status === 1,
      }));

      setUsers(formattedUsers);
      setFilteredUsers(formattedUsers);
    }
  }, []);

  const handleSearch = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value.toLowerCase();
    setSearch(value);
    setFilteredUsers(
      users.filter(
        (user) =>
          user.nome.toLowerCase().includes(value) ||
          user.email.toLowerCase().includes(value)
      )
    );
  };

  const handleToggle = async (index: number, field: "active" | "isAdmin") => {
    const updatedUsers = [...users];

    if (field === "active") {
      updatedUsers[index].active = !updatedUsers[index].active;
      updatedUsers[index].status = updatedUsers[index].active ? 1 : 2;
    } else if (field === "isAdmin") {
      updatedUsers[index].isAdmin = !updatedUsers[index].isAdmin;
      updatedUsers[index].authority = updatedUsers[index].isAdmin ? 1 : 0;
    }

    setUsers(updatedUsers);
    setFilteredUsers(updatedUsers);
    try {
      if (field === "active") {
        await Api.post("/admin/atualizaStatus", {
          id: updatedUsers[index].id,
          status: updatedUsers[index].status,
        });
      } else {
        await Api.post("/admin/atualizaPermissoes", {
          id: updatedUsers[index].id,
          perfil: updatedUsers[index].authority,
        });
      }

      const userManage = await Api.get("/admin/usuarios");
      localStorage.removeItem("userManagement");
      localStorage.setItem("userManagement", JSON.stringify(userManage.data));
    } catch (error: any) {
      console.error("Erro ao salvar alteração", error);
      Swal.fire({
        title: "Erro ao salvar alterações!",
        text: `${error.response.data.msg}`,
        icon: "error",
      });
    }
  };

  return (
    <div className="p-4">
      <div className="mb-4">
        <input
          type="text"
          placeholder="Buscar por nome ou email"
          className="p-2 border rounded w-full"
          value={search}
          onChange={handleSearch}
        />
      </div>

      <Paper sx={{ width: "100%", overflow: "hidden" }}>
        <TableContainer sx={{ maxHeight: 440 }}>
          <Table stickyHeader aria-label="sticky table">
            <TableHead>
              <TableRow>
                <TableCell align="left">Nome</TableCell>
                <TableCell align="left">Email</TableCell>
                <TableCell align="center">Ativo</TableCell>
                <TableCell align="center">Admin</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {filteredUsers.map((user, index) => (
                <TableRow
                  key={index}
                  sx={{ "&:last-child td, &:last-child th": { border: 0 } }}
                >
                  <TableCell align="left">{user.nome}</TableCell>
                  <TableCell align="left">{user.email}</TableCell>
                  <TableCell align="center">
                    <input
                      type="checkbox"
                      checked={user.active}
                      onChange={() => handleToggle(index, "active")}
                    />
                  </TableCell>
                  <TableCell align="center">
                    <input
                      type="checkbox"
                      checked={user.isAdmin}
                      onChange={() => handleToggle(index, "isAdmin")}
                    />
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      </Paper>
    </div>
  );
};

export default Usuarios;

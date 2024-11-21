import React, { useState, useEffect } from "react";
import defaultImg from "../assets/default-profile.jpg";
import TextField from "./TextField";
import DateField from "./DateField";
import PhoneField from "./PhoneField";
import SelectField from "./SelectField";
import Api from "../api/Api";
import Login from "./Login";
import Register from "./Register";
import Swal from "sweetalert2";

const Perfil = () => {
  const [userData, setUserData] = useState({
    anuncio: [],
    data_nascimento: "",
    email: "",
    endereco: "",
    id: null,
    imagemUrl: "",
    localidade: { id: null, cidade: "", estado: "" },
    nome: "",
    senha: "",
    telefone: "",
  });

  const [estados, setEstados] = useState<{ value: string; label: string }[]>([]);
  const [cidades, setCidades] = useState<{ value: string; label: string }[]>([]);
  const [estadoSelecionado, setEstadoSelecionado] = useState({value: "", label: "",});
  const [cidadeSelecionado, setCidadeSelecionado] = useState({value: "", label: "",});
  const [logginPop, setLogginPop] = useState(false);
  const [registerPop, setRegisterPop] = useState(false);

  useEffect(() => {
    const data = JSON.parse(localStorage.getItem("dadosUsuario") || "[]")[0];
    if (data) {
      setUserData({
        anuncio: data.anuncio || [],
        data_nascimento: data.data_nascimento || "",
        email: data.email || "",
        endereco: data.endereco || "",
        id: data.id || "",
        imagemUrl: data.imagemUrl || "",
        localidade: {
          id: data.localidade?.id || "",
          cidade: data.localidade?.cidade || "",
          estado: data.localidade?.estado || "",
        },
        nome: data.nome || "",
        senha: data.senha || "",
        telefone: data.telefone || "",
      });
      setEstadoSelecionado({
        value: data.localidade?.estado || "",
        label: data.localidade?.estado || "",
      });
      setCidadeSelecionado({
        value: data.localidade?.cidade || "",
        label: data.localidade?.cidade || "",
      });
    }
  }, []);

  // Buscar estados
  useEffect(() => {
    fetch("https://servicodados.ibge.gov.br/api/v1/localidades/estados")
      .then((response) => response.json())
      .then((data) => {
        const formattedStates = data
          .map((country: any) => ({
            value: country.id,
            label: country.nome,
          }))
          .sort((a: { label: string }, b: { label: any }) =>
            a.label.localeCompare(b.label)
          );
        setEstados(formattedStates);
      })
      .catch((error) => {console.error("Erro ao carregar os estados:", error);
        Swal.fire({
          title: "Erro ao carrgar os estados!",
          text: error.response.data.msg,
          icon: "error",
        });
      });
  }, []);

  // Buscar cidades quando um estado é selecionado
  useEffect(() => {
    const fetchCities = async () => {
      if (estadoSelecionado.value) {
        try {
          const response = await fetch(
            `https://servicodados.ibge.gov.br/api/v1/localidades/estados/${estadoSelecionado.value}/municipios`
          );
          const data = await response.json();
          const formattedCities = data.map((city: any) => ({
            value: city.id,
            label: city.nome,
          }));
          setCidades(formattedCities);
        } catch (error) {
          console.error("Erro ao carregar as cidades:", error);
        }
      }
    };
    fetchCities();
  }, [estadoSelecionado]);

  const handleImageChange: React.ChangeEventHandler<HTMLInputElement> = async (
    event
  ) => {
    const files = event.target.files;
    if (files && files.length > 0) {
      const file = files[0];
      const formData = new FormData();
      formData.append("file", file);

      try {
        const response = await Api.post("/usuario/fotoPerfil", formData, {
          headers: {
            "Content-Type": "multipart/form-data",
          },
        });
        if(response){
          const dadosUser = await Api.post("/usuario/dadosUsuario", {
            email: userData.email,
          });
  
          if (dadosUser.status === 200) {
            localStorage.setItem("dadosUsuario", JSON.stringify(dadosUser.data));
          }
        }

        const newImageUrl = response.data[0].imageUrl;
        setUserData((prevState) => ({
          ...prevState,
          imagemUrl: newImageUrl || "",
        }));
        Swal.fire({
          title: "Concluido!",
          text: `Imagem atualizada com sucesso!`,
          icon: "success",
          confirmButtonText: "OK",
        }).then((result) => {
          if(result.isConfirmed){
            window.location.reload();
          }
        });
      } catch (error:any) {
        console.error("Erro ao enviar a imagem:", error);
        Swal.fire({
          title: `Ocorreu um erro ao salvar a imagem.`,
          text: error.response.data.msg,
          icon: "error",
          confirmButtonText: "OK",
        }).then((result) => {
          if(result.isConfirmed){
            window.location.reload();
          }
        });
      }
    }
  };

  const handlePasswordClick = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { value } = e.target;
    setUserData((prevState) => ({ ...prevState, senha: value }));
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setUserData((prevState) => ({
      ...prevState,
      [name]: value,
    }));
  };

  const handleSave = async () => {
    if (
      cidadeSelecionado.label === userData.localidade.cidade &&
      estadoSelecionado.label !== userData.localidade.estado
    ) {
      alert("É obrigatório inserir Estado e Cidade respectivamente!");
      return;
    }

    const updatedUserData = {
      ...userData,
      localidade: {
        cidade: cidadeSelecionado.label || "",
        estado: estadoSelecionado.label,
      },
    };

    try {
      const retorno = await Api.post(
        "/usuario/atualizarDadosUsuario",
        updatedUserData
      );
      localStorage.setItem("dadosUsuario", JSON.stringify([retorno.data[0]]));
      alert("Alterações salvas!");
    } catch (error: any) {
      if (error.response && error.response.status === 403) {
        localStorage.clear();
        setLogginPop(true);
      } else {
        console.error("Erro ao salvar os dados:", error);
        alert("Ocorreu um erro ao salvar as alterações.");
      }
    }
  };

  return (
    <>
      {logginPop && (
        <Login setLogginPop={setLogginPop} setRegisterPop={setRegisterPop} />
      )}
      {registerPop && (
        <Register setLogginPop={setLogginPop} setRegisterPop={setRegisterPop} />
      )}
      <div className="p-6 max-w-full mx-auto flex flex-col gap-8 bg-slate-200">
        <div className="w-full flex flex-col md:flex-row gap-8 mb-4 justify-center">
          <div className="w-full md:w-1/4 text-center relative group ">
            <img
              src={userData.imagemUrl || defaultImg}
              alt="Profile"
              className="w-40 h-40 md:w-48 md:h-48 rounded-full object-cover mx-auto mb-2 shadow-lg"
            />
            <input
              type="file"
              accept="image/*"
              onChange={handleImageChange}
              className="hidden"
              id="imageUpload"
            />
            <label
              htmlFor="imageUpload"
              className=" w-40 h-40 md:w-48 md:h-48 rounded-full  mx-auto mb-2 absolute inset-0 bg-black bg-opacity-40 text-white text-sm flex items-center justify-center opacity-0 group-hover:opacity-100 cursor-pointer transition-opacity"
            >
              Alterar Imagem
            </label>
          </div>

          <div className="shadow-md p-6 rounded-lg bg-white w-full md:w-1/2">
            <h3 className="text-lg font-semibold mb-2">Login</h3>
            <div>
              <label className="block text-gray-600 text-sm mb-1">Nome</label>
              <TextField
                name="nome"
                icon="dataUser"
                value={userData.nome}
                onChange={handleInputChange}
              />
            </div>
            <div>
              <label className="block text-gray-600 text-sm mb-1">
                Data Nascimento
              </label>
              <DateField
                name="data_nascimento"
                value={userData.data_nascimento}
                onChange={handleInputChange}
              />
            </div>
            <div>
              <label className="block text-gray-600 text-sm mb-1">
                Telefone
              </label>
              <PhoneField
                name="telefone"
                value={userData.telefone}
                onChange={handleInputChange}
              />
            </div>
            <div>
              <label className="block text-gray-600 text-sm mb-1">Email</label>
              <TextField
                name="email"
                icon="email"
                value={userData.email}
                onChange={handleInputChange}
              />
            </div>
            <div>
              <label className="block text-gray-600 text-sm mb-1">Senha</label>
              <TextField
                placeholder="Altere sua senha"
                icon="pwd"
                onChange={handlePasswordClick}
                value={userData.senha}
              />
            </div>
          </div>

          <div className="shadow-md p-6 rounded-lg bg-white w-full md:w-1/2">
            <h3 className="text-lg font-semibold mb-2">Localidade</h3>
            <div>
              <label className="block text-gray-600 text-sm mb-1">
                Endereço
              </label>
              <TextField
                name="endereco"
                icon="dataUser"
                value={userData.endereco}
                onChange={handleInputChange}
              />
            </div>
            <div>
              <label className="block text-gray-600 text-sm mb-1">Estado</label>
              <SelectField
                placeholder={userData.localidade.estado}
                options={estados}
                value={estadoSelecionado}
                onChange={(e) => {
                  const selectedOption = estados.find(
                    (option) => Number(option.value) === Number(e.target.value)
                  );
                  if (selectedOption) {
                    setEstadoSelecionado(selectedOption);
                  }
                }}
              />
            </div>
            <div>
              <label className="block text-gray-600 text-sm mb-1">Cidade</label>
              <SelectField
                placeholder={userData.localidade.cidade}
                msgopcional="Selecione o Estado primeiro."
                options={cidades}
                value={cidadeSelecionado}
                onChange={(e) => {
                  const selectedOption = cidades.find(
                    (option) => Number(option.value) === Number(e.target.value)
                  );
                  if (selectedOption) {
                    setCidadeSelecionado(selectedOption);
                  }
                }}
              />
            </div>

        <button
          onClick={handleSave}
          className="w-full mt-16 bg-greenLogo text-white border border-greenLogo py-2 rounded-lg shadow-md hover:bg-white hover:text-greenLogo transition active:bg-slate-400"
        >
          Salvar Alterações
        </button>
          </div>
        </div>
      </div>
    </>
  );
};

export default Perfil;

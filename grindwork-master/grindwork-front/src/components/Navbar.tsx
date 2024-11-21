import Logo from "../assets/logo.png";
import { FaUser } from "react-icons/fa";
import Login from "./Login";
import { useEffect, useRef, useState } from "react";
import Register from "./Register";
import { Link, useNavigate } from "react-router-dom";
import { isUserLoggedIn } from "../api/authUtils";
import EsqueceuSenha from "./EsqueceuSenha";

const Navbar = () => {
  const navigate = useNavigate();

  const [logginPop, setLogginPop] = useState(false);
  const [registerPop, setRegisterPop] = useState(false);
  const [forgottPop, setForgottPop] = useState(false);
  const [userName, setUserName] = useState("");
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const [isAdmin, setIsAdmin] = useState(false);
  const menuRef = useRef<HTMLDivElement>(null);

  const handleCriarAnuncio = () => {
    if (isUserLoggedIn()) {
      navigate("/cadastrar-anuncio");
    } else {
      setLogginPop(true);
    }
  };

  const handleLogout = () => {
    localStorage.clear();
    navigate("/");
    window.location.reload();
  };

  useEffect(() => {
    const dadosUsuario = localStorage.getItem("dadosUsuario");
    if (dadosUsuario) {
      const dadosUserArray = JSON.parse(dadosUsuario);
      const storedUserName = dadosUserArray[0].nome;
      const authorityUser = dadosUserArray[0].authorities[0].authority;
      if (storedUserName) {
        setUserName(storedUserName);
        if (authorityUser == "ROLE_ADMIN") {
          setIsAdmin(true);
        }
      }
    }
  }, []);

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (menuRef.current && !menuRef.current.contains(event.target as Node)) {
        setIsMenuOpen(false);
      }
    };

    document.addEventListener("mousedown", handleClickOutside);
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, []);

  return (
    <>
      <div className="flex justify-between p-2 bg-greenLogo">
        <Link to={"/"}>
          <img src={Logo} alt="grindwork-logo" className="w-30 h-10 m-2 " />
        </Link>
        <div className="flex ">
          {userName ? (
            <span
              onClick={() => setIsMenuOpen(true)}
              className="cursor-pointer"
            >
              <h1 className="flex font-bold text-lg h-12 p-3 ml-10 text-slate-100 ">
                Bem Vindo, {userName.split(" ")[0]}
              </h1>
            </span>
          ) : (
            <div
              onClick={() => setLogginPop(true)}
              className="flex  h-12 p-3 ml-10 cursor-pointer text-teal-50 hover:text-slate-300 active:text-orangeLogo focus:outline-none focus:ring focus:ring-teal-300"
            >
              <FaUser className="w-25 h-25 mt-1" />
              <h1 className="font-bold text-lg ml-1">Entrar</h1>
            </div>
          )}

          <div
            onClick={handleCriarAnuncio}
            className="flex  w-28 h-12 p-3 ml-6 cursor-pointer rounded-md border bg-white text-greenLogo hover:bg-greenLogo hover:text-white active:bg-orangeLogo focus:outline-none focus:ring focus:ring-white transition duration-300"
          >
            <h1 className="font-bold text-lg  ml-1">Anunciar</h1>
          </div>
        </div>
      </div>
      {logginPop && (
        <Login
          setLogginPop={setLogginPop}
          setRegisterPop={setRegisterPop}
          setForgottPop={setForgottPop}
        />
      )}

      {forgottPop && <EsqueceuSenha setForgottPop={setForgottPop} />}

      {registerPop && (
        <Register setLogginPop={setLogginPop} setRegisterPop={setRegisterPop} />
      )}

      {isMenuOpen && (
        <div
          ref={menuRef}
          onClick={(e) => e.stopPropagation()}
          className="absolute right-32 mt-16 w-48 bg-white rounded-md shadow-lg z-50"
        >
          <ul className="py-2">
            <li>
              <a
                href="/perfil"
                className="block px-4 py-2 text-gray-800 hover:bg-gray-200"
              >
                Perfil
              </a>
            </li>
            {isAdmin && (
              <li>
                <a
                  href="/usuarios"
                  className="block px-4 py-2 text-gray-800 hover:bg-gray-200"
                >
                  Usuários
                </a>
              </li>
            )}

            <li>
              <a
                href="/meus-anuncios"
                className="block px-4 py-2 text-gray-800 hover:bg-gray-200"
              >
                Meus Anúncios
              </a>
            </li>
            <li>
              <span
                onClick={handleLogout}
                className="block px-4 py-2 text-gray-800 hover:bg-gray-200 cursor-pointer"
              >
                Sair
              </span>
            </li>
          </ul>
        </div>
      )}
    </>
  );
};

export default Navbar;
